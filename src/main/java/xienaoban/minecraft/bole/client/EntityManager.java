package xienaoban.minecraft.bole.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.registry.Registry;
import xienaoban.minecraft.bole.util.Keys;
import xienaoban.minecraft.bole.util.TreeNodeExecutor;

import java.lang.reflect.Modifier;
import java.util.*;

@Environment(EnvType.CLIENT)
public class EntityManager {
    private static EntityManager instance = null;

    private final Map<Class<?>, EntityTreeNode> tree = new HashMap<>();
    private final Map<EntityType<?>, EntityInfo> infos = new HashMap<>();

    private final Map<String, TagGroup> tagGroups = new HashMap<>();

    private final TagGroup defaultTags = new TagGroup(Keys.TAG_GROUP_DEFAULT);
    private final TagGroup classTags = new TagGroup(Keys.TAG_GROUP_CLASS);
    private final TagGroup interfaceTags = new TagGroup(Keys.TAG_GROUP_INTERFACE);
    private final TagGroup namespaceTags = new TagGroup(Keys.TAG_GROUP_NAMESPACE);

    /**
     * Don't invoke it before you join a world.
     */
    public static EntityManager getInstance() {
        if (instance == null) {
            instance = new EntityManager();
        }
        return instance;
    }

    private EntityManager() {
        initEntityInfos();
        initJavaTags();
        initDefaultTags();
        registerTagGroup(this.defaultTags);
        registerTagGroup(this.classTags);
        registerTagGroup(this.interfaceTags);
        registerTagGroup(this.namespaceTags);

        // this.tree.values().forEach(node -> System.out.println("1 " + node.getClazz().getSimpleName() + " " + node.getSons()));
        // this.infos.values().forEach(info -> System.out.println("2 " + info.getType().toString()));
        // this.classTags.getTags().forEach(tag -> System.out.println("3 " + tag.getName() + " " + tag.getEntities()));
        // this.interfaceTags.getTags().forEach(tag -> System.out.println("4 " + tag.getName() + " " + tag.getEntities()));
        // this.namespaceTags.getTags().forEach(tag -> System.out.println("5 " + tag.getName() + " " + tag.getEntities()));
    }

    private void initEntityInfos() {
        this.tree.put(Entity.class, new EntityTreeNode());
        for (EntityType<?> entityType : Registry.ENTITY_TYPE) {
            EntityInfo entityInfo;
            try { entityInfo = new EntityInfo(entityType); }
            catch (Exception e) { continue; }
            this.infos.put(entityInfo.getType(), entityInfo);
            getEntityTreeNode(entityInfo.getClazz());
        }
    }

    private void initJavaTags() {
        dfsEntityTree(true, (root, depth) -> {
            if (!Modifier.isAbstract(root.getClazz().getModifiers())) {
                return false;
            }
            this.classTags.addTag(root.clazz.getName(), root.getFather().clazz.getName());
            return true;
        });
        for (EntityInfo entityInfo : this.infos.values()) {
            this.namespaceTags.addToTag(EntityType.getId(entityInfo.getType()).getNamespace(), entityInfo);
            Class<?> clazz = entityInfo.getClazz();
            Class<?> t = Entity.class.getSuperclass();
            while (!t.equals(clazz)) {
                if (Modifier.isAbstract(clazz.getModifiers())) {
                    this.classTags.addToTag(clazz.getName(), entityInfo);
                }
                for (Class<?> clazz2 : clazz.getInterfaces()) {
                    if (!clazz2.getName().contains("Mixin")) {
                        this.interfaceTags.addToTag(clazz2.getName(), entityInfo);
                    }
                }
                clazz = clazz.getSuperclass();
            }
        }
    }

    private void initDefaultTags() {

    }

    public void registerTagGroup(TagGroup group) {
        this.tagGroups.put(group.getName(), group);
    }

    public TagGroup getTagGroup(String tagName) {
        return this.tagGroups.get(tagName);
    }

    public Collection<TagGroup> getTagGroups() {
        return this.tagGroups.values();
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

    public void dfsEntityTree(boolean skipRoot, TreeNodeExecutor<EntityTreeNode> executor) {
        EntityTreeNode root = getEntityTreeNode(Entity.class);
        if (skipRoot) {
            root.getSons().forEach(son -> dfsEntityTreePrivate(son, 1, executor));
        }
        else {
            dfsEntityTreePrivate(root, 0, executor);
        }
    }

    private void dfsEntityTreePrivate(EntityTreeNode root, int depth, TreeNodeExecutor<EntityTreeNode> executor) {
        if (executor.execute(root, depth)) {
            int d2 = depth + 1;
            root.getSons().forEach(son -> dfsEntityTreePrivate(son, d2, executor));
        }
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
            this.text = new TranslatableText(tagGroupName);
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

        public Collection<Tag> getRootTags() {
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

    public static class Tag {
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
            this.text = new TranslatableText(name);
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
    }

    public static class EntityInfo {
        private final EntityType<?> type;
        private final Class<?> clazz;
        private final List<Tag> tags;

        public EntityInfo(EntityType<?> type) {
            this.type = type;
            Entity instance = type.create(MinecraftClient.getInstance().world);
            if (!(instance instanceof LivingEntity)) {
                throw new RuntimeException();
            }
            this.clazz = instance.getClass();
            this.tags = new ArrayList<>();
        }

        public EntityType<?> getType() {
            return this.type;
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

        @Override
        public String toString() {
            return this.type.toString();
        }
    }
}
