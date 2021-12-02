package xienaoban.minecraft.bole.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.registry.Registry;
import xienaoban.minecraft.bole.util.Keys;

import java.lang.reflect.Modifier;
import java.util.*;

@Environment(EnvType.CLIENT)
public class EntityManager {
    private static EntityManager instance = null;

    private final Map<Class<?>, EntityTreeNode> tree = new HashMap<>();
    private final Map<EntityType<?>, EntityInfo> infos = new HashMap<>();

    private final TagGroup classTags = new TagGroup(Keys.TAG_GROUP_CLASS);
    private final TagGroup interfaceTags = new TagGroup(Keys.TAG_GROUP_INTERFACE);
    private final TagGroup namespaceTags = new TagGroup(Keys.TAG_GROUP_NAMESPACE);

    public static EntityManager getInstance() {
        if (instance == null) {
            instance = new EntityManager();
        }
        return instance;
    }

    private EntityManager() {
        init();
    }

    public void init() {
        this.tree.put(Entity.class, new EntityTreeNode());
        Registry.ENTITY_TYPE.stream().forEach((entityType -> {
            EntityInfo entityInfo;
            try { entityInfo = new EntityInfo(entityType); }
            catch (Exception e) { return; }
            this.infos.put(entityInfo.getType(), entityInfo);
            namespaceTags.addTag(EntityType.getId(entityType).getNamespace(), entityInfo);
            Class<?> clazz = entityInfo.getClazz();
            getEntityTreeNode(clazz);
            Class<?> t = Entity.class.getSuperclass();
            while (!t.equals(clazz)) {
                if (Modifier.isAbstract(clazz.getModifiers())) {
                    classTags.addTag(clazz.getName(), entityInfo);
                }
                for (Class<?> clazz2 : clazz.getInterfaces()) {
                    if (!clazz2.getName().contains("Mixin")) {
                        interfaceTags.addTag(clazz2.getName(), entityInfo);
                    }
                }
                clazz = clazz.getSuperclass();
            }
        }));
        // this.tree.values().forEach(node -> System.out.println("1 " + node.getClazz().getSimpleName() + " " + node.getSons()));
        // this.infos.values().forEach(info -> System.out.println("2 " + info.getType().toString()));
        // this.classTags.getTags().forEach(tag -> System.out.println("3 " + tag.getName() + " " + tag.getEntities()));
        // this.interfaceTags.getTags().forEach(tag -> System.out.println("4 " + tag.getName() + " " + tag.getEntities()));
        // this.namespaceTags.getTags().forEach(tag -> System.out.println("5 " + tag.getName() + " " + tag.getEntities()));
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
        private final Map<String, Tag> tags;

        public TagGroup(String tagGroupName) {
            this.name = tagGroupName;
            this.tags = new HashMap<>();
        }

        public String getName() {
            return name;
        }

        public Collection<Tag> getTags() {
            return this.tags.values();
        }

        public Tag getTag(String tagName) {
            Tag tag = this.tags.getOrDefault(tagName, null);
            if (tag == null) {
                tag = new Tag(this, tagName);
                this.tags.put(tagName, tag);
            }
            return tag;
        }

        public void addTag(String tagName, EntityInfo entityInfo) {
            Tag tag = getTag(tagName);
            tag.addEntity(entityInfo);
            entityInfo.addTag(tag);
        }
    }

    public static class Tag {
        private final TagGroup tagGroup;
        private final String name;
        private final List<EntityInfo> entities;

        public Tag(TagGroup tagGroup, String name) {
            this.tagGroup = tagGroup;
            this.name = name;
            this.entities = new ArrayList<>();
        }

        public TagGroup getTagGroup() {
            return tagGroup;
        }

        public String getName() {
            return this.name;
        }

        public List<EntityInfo> getEntities() {
            return this.entities;
        }

        protected void addEntity(EntityInfo info) {
            this.entities.add(info);
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
