package xienaoban.minecraft.bole.client.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.NotNull;
import xienaoban.minecraft.bole.Bole;
import xienaoban.minecraft.bole.util.Keys;
import xienaoban.minecraft.bole.util.MiscUtil;
import xienaoban.minecraft.bole.util.TreeNodeExecutor;

import java.io.*;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Environment(EnvType.CLIENT)
public class EntityManager {
    private static volatile EntityManager instance = null;

    private static final String MISC_PATH = "misc/";
    private static final String ENTITY_SORT_ORDER_PATH = MISC_PATH + "entity_sort_order.txt";

    private final Map<Class<?>, EntityTreeNode> tree = new HashMap<>();
    private final Map<EntityType<?>, EntityInfo> infos = new HashMap<>();
    private final List<EntityInfo> sortedInfos = new ArrayList<>();

    private final Map<String, Integer> entitySortIds = new HashMap<>();
    private final Map<Class<?>, String> deobfuscation = new HashMap<>();

    private final List<TagGroup> tagGroups = new ArrayList<>();

    private final TagGroup defaultTags = new TagGroup(Keys.TAG_GROUP_DEFAULT);
    private final TagGroup classTags = new TagGroup(Keys.TAG_GROUP_CLASS);
    private final TagGroup interfaceTags = new TagGroup(Keys.TAG_GROUP_INTERFACE);
    private final TagGroup namespaceTags = new TagGroup(Keys.TAG_GROUP_NAMESPACE);

    /**
     * Don't invoke it before joining a world.
     */
    public static EntityManager getInstance() {
        if (instance == null) {
            synchronized (EntityManager.class) {
                if (instance == null) {
                    instance = new EntityManager();
                    Bole.LOGGER.info("EntityManager of Bole initialized.");
                }
            }
        }
        return instance;
    }

    public static void init() {
        initEntitySortOrderFile();
    }

    public static void initEntitySortOrderFile() {
        Path orderPath = Keys.ENTITY_SORT_ORDER_CONFIG_PATH();
        if (orderPath.toFile().isFile()) {
            return;
        }
        try (BufferedWriter fileWriter = MiscUtil.getFileWriter(orderPath);
             BufferedReader resourceReader = MiscUtil.getResourceReader("/" + ENTITY_SORT_ORDER_PATH)) {
            fileWriter.write("# This file controls the order of entities displayed in bole handbook homepage.");
            fileWriter.newLine();
            String line;
            while ((line = resourceReader.readLine()) != null) {
                if (line.isEmpty() || line.charAt(0) == '#') {
                    continue;
                }
                fileWriter.write(line);
                fileWriter.newLine();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private EntityManager() {
        initEntitySortIds();
        initEntityInfos();
        // This method is only called when updating mc version
        // EntityDeobfuscation.__generateDeobfuscationMapCode(this);
        initDeobfuscation();
        initJavaTags();
        initDefaultTags();
        registerTagGroup(this.defaultTags);
        registerTagGroup(this.classTags);
        registerTagGroup(this.interfaceTags);
        registerTagGroup(this.namespaceTags);
        sortAllEntities();
    }

    private void initEntitySortIds() {
        Map<String, Integer> orderMap = this.entitySortIds;
        Path orderPath = Keys.ENTITY_SORT_ORDER_CONFIG_PATH();
        initEntitySortOrderFile();
        try (BufferedReader orderReader = MiscUtil.getFileReader(orderPath)) {
            if (!orderMap.isEmpty()) {
                orderMap.clear();
            }
            int sid = 0;
            String line;
            while ((line = orderReader.readLine()) != null) {
                if (line.isEmpty() || line.charAt(0) == '#') {
                    continue;
                }
                orderMap.put(line.trim(), sid++);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initEntityInfos() {
        this.tree.put(Entity.class, new EntityTreeNode());
        for (EntityType<?> entityType : Registry.ENTITY_TYPE) {
            EntityInfo entityInfo;
            try { entityInfo = new EntityInfo(entityType); }
            catch (Exception e) { continue; }
            this.infos.put(entityInfo.getType(), entityInfo);
            this.sortedInfos.add(entityInfo);
            getEntityTreeNode(entityInfo.getClazz());
        }
        // [Should I?] this.infos.put(EntityType.PLAYER, new EntityInfo(EntityType.PLAYER, PlayerEntity.class));
        // [Should I?] getEntityTreeNode(PlayerEntity.class);
        initEntityInfoSortIds();
    }

    private void initEntityInfoSortIds() {
        this.sortedInfos.sort((a, b) -> {
            Identifier ia = EntityType.getId(a.getType());
            Identifier ib = EntityType.getId(b.getType());
            Integer sortIdA = this.entitySortIds.getOrDefault(ia.toString(), null);
            Integer sortIdB = this.entitySortIds.getOrDefault(ib.toString(), null);
            if (sortIdA != null && sortIdB != null) {
                return sortIdA - sortIdB;
            }
            else if (sortIdA != null || sortIdB != null) {
                return sortIdA == null ? 1 : -1;
            }
            int cmp = ia.getNamespace().compareTo(ib.getNamespace());
            if (cmp != 0) {
                if (ia.getNamespace().equals(Identifier.DEFAULT_NAMESPACE)) {
                    return 1;
                }
                return cmp;
            }
            String pa = ia.getPath(), pb = ib.getPath();
            int i = pa.length() - 1, j = pb.length() - 1;
            while (i >= 0 && j >= 0) {
                cmp = pa.charAt(i) - pb.charAt(j);
                if (cmp != 0) {
                    return cmp;
                }
                --i; --j;
            }
            return i - j;
        });
        for (int i = this.sortedInfos.size() - 1; i >= 0; --i) {
            this.sortedInfos.get(i).setSortId(i);
        }
    }

    private void initDeobfuscation() {
        EntityDeobfuscation.buildDeobfuscationMap(this.deobfuscation);
    }

    private void initJavaTags() {
        dfsEntityTree(true, (root, depth) -> {
            if (!Modifier.isAbstract(root.getClazz().getModifiers())) {
                return false;
            }
            this.classTags.addTag(getClassId(root.getClazz()), getClassId(root.getFather().getClazz()));
            return true;
        });
        for (EntityInfo entityInfo : getEntityInfos()) {
            this.namespaceTags.addToTag(EntityType.getId(entityInfo.getType()).getNamespace(), entityInfo);
            Class<?> clazz = entityInfo.getClazz();
            Class<?> root = Entity.class;
            while (!root.equals(clazz)) {
                if (Modifier.isAbstract(clazz.getModifiers())) {
                    this.classTags.addToTag(getClassId(clazz), entityInfo);
                }
                for (Class<?> clazz2 : clazz.getInterfaces()) {
                    if (!clazz2.getSimpleName().contains("Mixin")) {
                        this.interfaceTags.addToTag(getClassId(clazz2), entityInfo);
                    }
                }
                clazz = clazz.getSuperclass();
            }
        }
    }

    private void initDefaultTags() {
        this.defaultTags.addTag(Keys.TAG_DEFAULT_TERRESTRIAL_ANIMAL, Keys.TAG_DEFAULT_ANIMAL);
        this.defaultTags.addTag(Keys.TAG_DEFAULT_HUMAN, Keys.TAG_DEFAULT_TERRESTRIAL_ANIMAL);
        this.defaultTags.addTag(Keys.TAG_DEFAULT_AQUATIC_ANIMAL, Keys.TAG_DEFAULT_ANIMAL);
        this.defaultTags.addTag(Keys.TAG_DEFAULT_HUMANOID, Keys.TAG_DEFAULT_MONSTER);
        this.defaultTags.addTag(Keys.TAG_DEFAULT_PATROL, Keys.TAG_DEFAULT_MONSTER);

        this.defaultTags.addAllToTag(Keys.TAG_DEFAULT_HUMAN, this.classTags.getTag(getClassId(MerchantEntity.class)).getEntities());
        // this.defaultTags.addAllToTag(Keys.TAG_DEFAULT_HUMAN, this.classTags.getTag(getClassId(PlayerEntity.class)).getEntities());

        this.defaultTags.addAllToTag(Keys.TAG_DEFAULT_TERRESTRIAL_ANIMAL, this.classTags.getTag(getClassId(AnimalEntity.class)).getEntities());
        this.defaultTags.addAllToTag(Keys.TAG_DEFAULT_TERRESTRIAL_ANIMAL, this.defaultTags.getTag(Keys.TAG_DEFAULT_HUMAN).getEntities());
        this.defaultTags.addAllToTag(Keys.TAG_DEFAULT_TERRESTRIAL_ANIMAL, List.of(getEntityInfo(EntityType.BAT), getEntityInfo(EntityType.ALLAY), getEntityInfo(EntityType.SPIDER), getEntityInfo(EntityType.CAVE_SPIDER), getEntityInfo(EntityType.SILVERFISH)));

        this.defaultTags.addAllToTag(Keys.TAG_DEFAULT_AQUATIC_ANIMAL, this.classTags.getTag(getClassId(WaterCreatureEntity.class)).getEntities());
        this.defaultTags.addToTag(Keys.TAG_DEFAULT_AQUATIC_ANIMAL, getEntityInfo(EntityType.TURTLE));
        this.defaultTags.addToTag(Keys.TAG_DEFAULT_AQUATIC_ANIMAL, getEntityInfo(EntityType.AXOLOTL));
        this.defaultTags.addToTag(Keys.TAG_DEFAULT_AQUATIC_ANIMAL, getEntityInfo(EntityType.FROG));

        this.defaultTags.addAllToTag(Keys.TAG_DEFAULT_ANIMAL, this.defaultTags.getTag(Keys.TAG_DEFAULT_TERRESTRIAL_ANIMAL).getEntities());
        this.defaultTags.addAllToTag(Keys.TAG_DEFAULT_ANIMAL, this.defaultTags.getTag(Keys.TAG_DEFAULT_AQUATIC_ANIMAL).getEntities());

        List<EntityInfo> cHumanoid = this.getEntityInfos().stream().filter(entityInfo -> {
            if (entityInfo.getInstance() instanceof HostileEntity) {
                if (entityInfo.getClazz() == CreeperEntity.class || entityInfo.getClazz() == BlazeEntity.class
                        || entityInfo.getClazz() == WitherEntity.class) return false;
                Box box = entityInfo.getInstance().getBoundingBox();
                return box.getXLength() < 1 && box.getYLength() > 1.6;
            }
            return false;
        }).collect(Collectors.toList());
        this.defaultTags.addAllToTag(Keys.TAG_DEFAULT_HUMANOID, cHumanoid);

        this.defaultTags.addAllToTag(Keys.TAG_DEFAULT_PATROL, this.classTags.getTag(getClassId(PatrolEntity.class)).getEntities());

        Stream<EntityInfo> sMonster = getEntityInfos().stream().filter(entityInfo -> entityInfo.getType().getSpawnGroup() == SpawnGroup.MONSTER);
        this.defaultTags.addAllToTag(Keys.TAG_DEFAULT_MONSTER, sMonster.collect(Collectors.toSet()));

        Set<EntityInfo> tAnimal = new HashSet<>(this.defaultTags.getTag(Keys.TAG_DEFAULT_ANIMAL).getEntities());
        Set<EntityInfo> tMonster = new HashSet<>(this.defaultTags.getTag(Keys.TAG_DEFAULT_MONSTER).getEntities());
        List<EntityInfo> other = getEntityInfos().stream().filter(entityInfo -> !(tAnimal.contains(entityInfo) || tMonster.contains(entityInfo))).toList();
        this.defaultTags.addAllToTag(Keys.TAG_DEFAULT_OTHER, other);
    }

    public void registerTagGroup(TagGroup group) {
        this.tagGroups.add(group);
    }

    private void sortAllEntities() {
        for (TagGroup group : getTagGroups()) {
            boolean noSkip = !group.getName().equals(Keys.TAG_GROUP_DEFAULT);
            if (noSkip) {
                if (group.getName().equals(Keys.TAG_GROUP_INTERFACE)) {
                    group.getRootTags().sort((a, b) -> {
                        boolean mca = a.getName().indexOf("net.minecraft") == 0;
                        boolean mcb = b.getName().indexOf("net.minecraft") == 0;
                        if (mca ^ mcb) return mca ? -1 : 1;
                        return a.getName().compareTo(b.getName());
                    });
                }
                else Collections.sort(group.getRootTags());
            }
            // deduplication
            for (Tag tag : group.getTags()) {
                if (noSkip) Collections.sort(tag.getSons());
                List<EntityInfo> entities = tag.getEntities();
                Collections.sort(entities);
                for (int i = entities.size() - 2; i >= 0; --i) {
                    if (entities.get(i) == entities.get(i + 1)) {
                        entities.remove(i);
                    }
                }
            }
        }

        List<Tag> ns = this.namespaceTags.getRootTags();
        Tag mcTag = this.namespaceTags.getTag(Identifier.DEFAULT_NAMESPACE);
        ns.remove(mcTag);
        Collections.sort(ns);
        ns.add(0, mcTag);
    }

    /**
     * Re-read the config file "bole_entity_sort_order.txt" and sort the entities in the homepage.
     */
    public void reorderAllEntities() {
        initEntitySortIds();
        initEntityInfoSortIds();
        sortAllEntities();
    }

    public List<TagGroup> getTagGroups() {
        return this.tagGroups;
    }

    public EntityTreeNode getEntityTreeNode(Class<?> clazz) {
        EntityTreeNode node = this.tree.getOrDefault(clazz, null);
        if (node == null) {
            node = new EntityTreeNode(clazz, getEntityTreeNode(clazz.getSuperclass()));
            this.tree.put(clazz, node);
        }
        return node;
    }

    public EntityInfo getEntityInfo(EntityType<?> entityType) {
        return this.infos.get(entityType);
    }

    public List<EntityInfo> getEntityInfos() {
        return this.sortedInfos;
    }

    public String getClassDeobfuscation(Class<?> clazz) {
        return this.deobfuscation.getOrDefault(clazz, clazz.getName());
    }

    private String getClassId(Class<?> clazz) {
        return getClassDeobfuscation(clazz);
    }

    public void dfsEntityTree(boolean skipRoot, TreeNodeExecutor<EntityTreeNode> executor) {
        dfsEntityTree(skipRoot, executor, TreeNodeExecutor.empty());
    }

    public void dfsEntityTree(boolean skipRoot, TreeNodeExecutor<EntityTreeNode> frontExecutor, TreeNodeExecutor<EntityTreeNode> rearExecutor) {
        EntityTreeNode root = getEntityTreeNode(Entity.class);
        if (skipRoot) {
            root.getSons().forEach(son -> dfsEntityTreePrivate(son, 1, frontExecutor, rearExecutor));
        }
        else {
            dfsEntityTreePrivate(root, 0, frontExecutor, rearExecutor);
        }
    }

    private void dfsEntityTreePrivate(EntityTreeNode root, int depth, TreeNodeExecutor<EntityTreeNode> frontExecutor, TreeNodeExecutor<EntityTreeNode> rearExecutor) {
        if (frontExecutor.execute(root, depth)) {
            int d2 = depth + 1;
            root.getSons().forEach(son -> dfsEntityTreePrivate(son, d2, frontExecutor, rearExecutor));
        }
        rearExecutor.execute(root, depth);
    }

    public static class EntityTreeNode {
        private final Class<?> clazz;
        private final EntityTreeNode father;
        private final List<EntityTreeNode> sons;

        public EntityTreeNode() {
            this.clazz = Entity.class;
            this.father = null;
            this.sons = new ArrayList<>();
        }

        public EntityTreeNode(Class<?> clazz, EntityTreeNode father) {
            this.clazz = clazz;
            this.father = father;
            this.sons = new ArrayList<>();
            father.addSon(this);
        }

        public Class<?> getClazz() {
            return this.clazz;
        }

        public EntityTreeNode getFather() {
            return this.father;
        }

        public List<EntityTreeNode> getSons() {
            return this.sons;
        }

        protected void addSon(EntityTreeNode son) {
            this.sons.add(son);
        }

        @Override
        public String toString() {
            return this.clazz.getSimpleName();
        }
    }

    public static class TagGroup {
        private final String name;
        private final Text text;
        private final Map<String, Tag> tags;
        private final List<Tag> rootTags;

        public TagGroup(String tagGroupName) {
            this.name = tagGroupName;
            this.text = Text.translatable(tagGroupName);
            this.tags = new HashMap<>();
            this.rootTags = new ArrayList<>();
        }

        public String getName() {
            return name;
        }

        public Text getText() {
            return text;
        }

        public Collection<Tag> getTags() {
            return this.tags.values();
        }

        public List<Tag> getRootTags() {
            return this.rootTags;
        }

        public Tag getTag(String tagName) {
            Tag tag = this.tags.getOrDefault(tagName, null);
            if (tag == null) {
                tag = new Tag(tagName);
                this.tags.put(tagName, tag);
                this.rootTags.add(tag);
            }
            return tag;
        }

        public void addTag(String tagName, String fatherTagName) {
            Tag old = this.tags.put(tagName, new Tag(tagName, getTag(fatherTagName)));
            if (old != null) {
                throw new RuntimeException("Tag \"" + tagName + "\" already exists.");
            }
        }

        public void addToTag(String tagName, EntityInfo entityInfo) {
            Tag tag = getTag(tagName);
            tag.addEntity(entityInfo);
            entityInfo.addTag(tag);
        }

        public void addAllToTag(String tagName, Collection<EntityInfo> entityInfos) {
            Tag tag = getTag(tagName);
            tag.addEntities(entityInfos);
            entityInfos.forEach(entityInfo -> entityInfo.addTag(tag));
        }

        public void removeFromTag(String tagName, EntityInfo entityInfo) {
            Tag tag = getTag(tagName);
            tag.removeEntity(entityInfo);
            entityInfo.removeTag(tag);
        }

        public void dfsTags(TreeNodeExecutor<Tag> executor) {
            for (Tag root : getRootTags()) {
                dfsTagsPrivate(root, 0, executor);
            }
        }

        private void dfsTagsPrivate(Tag root, int depth, TreeNodeExecutor<Tag> executor) {
            if (executor.execute(root, depth)) {
                int d2 = depth + 1;
                root.getSons().forEach(son -> dfsTagsPrivate(son, d2, executor));
            }
        }
    }

    public static class Tag implements Comparable<Tag> {
        private final String name;
        private final Text text;
        private final List<EntityInfo> entities;
        private final Tag father;
        private final List<Tag> sons;

        public Tag(String name) {
            this(name, null);
        }

        public Tag(String name, Tag father) {
            this.name = name;
            this.text = Text.translatable(name);
            this.entities = new ArrayList<>();
            this.father = father;
            if (father != null) {
                father.addSon(this);
            }
            this.sons = new ArrayList<>();
        }

        public String getName() {
            return this.name;
        }

        public Text getText() {
            return text;
        }

        public List<EntityInfo> getEntities() {
            return this.entities;
        }

        protected void addEntity(EntityInfo info) {
            this.entities.add(info);
        }

        protected void addEntities(Collection<EntityInfo> infos) {
            this.entities.addAll(infos);
        }

        protected void removeEntity(EntityInfo info) {
            this.entities.remove(info);
        }

        public Tag getFather() {
            return father;
        }

        public void addSon(Tag tag) {
            this.sons.add(tag);
        }

        public List<Tag> getSons() {
            return this.sons;
        }

        @Override
        public String toString() {
            return name;
        }

        @Override
        public int compareTo(@NotNull Tag o) {
            String s1 = getName().substring(getName().lastIndexOf('.') + 1);
            String s2 = o.getName().substring(o.getName().lastIndexOf('.') + 1);
            return s1.compareTo(s2);
        }
    }

    public static class EntityInfo implements Comparable<EntityInfo> {
        private final EntityType<?> type;
        private final Entity instance;
        private final Class<?> clazz;
        private final List<Tag> tags;
        private int sortId;

        public EntityInfo(EntityType<?> type) {
            this.type = type;
            Entity instance = type.create(MinecraftClient.getInstance().world);
            this.instance = instance;
            if (!(instance instanceof LivingEntity)) {
                throw new RuntimeException();
            }
            this.clazz = instance.getClass();
            this.tags = new ArrayList<>();
        }

        public EntityType<?> getType() {
            return this.type;
        }

        public Entity getInstance() {
            return this.instance;
        }

        public Class<?> getClazz() {
            return this.clazz;
        }

        public List<Tag> getTags() {
            return this.tags;
        }

        protected void addTag(Tag tag) {
            this.tags.add(tag);
        }

        protected void removeTag(Tag tag) {
            this.tags.remove(tag);
        }

        public void setSortId(int sortId) {
            this.sortId = sortId;
        }

        @Override
        public String toString() {
            return this.type.toString();
        }

        @Override
        public int compareTo(@NotNull EntityInfo o) {
            return this.sortId - o.sortId;
        }
    }
}