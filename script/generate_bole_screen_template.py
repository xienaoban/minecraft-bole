import re, os

bole_screen_template = '''package xienaoban.minecraft.bole.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class Bole{#0}Screen<E extends {#0}, H extends Bole{#0}ScreenHandler<E>> extends Bole{#2}Screen<E, H> {
    public Bole{#0}Screen(H handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        // code here
    }

    @Override
    protected void initCustom() {
        this.entityDisplayPlan = chooseEntityDisplayPlan(this.curLeftPage);
        this.curRightPage.addSlot(new CustomNameContentWidget());
        this.curRightPage.addSlot(new BoundingBoxContentWidget());
        this.curRightPage.addSlot(new NetherPortalCooldownContentWidget());
        // code here
    }

    @Override
    protected void drawLeftContent(MatrixStack matrices, float delta, int x, int y, int mouseX, int mouseY) {
        super.drawLeftContent(matrices, delta, x, y, mouseX, mouseY);
        // code here
    }

    @Override
    protected void drawRightContent(MatrixStack matrices, float delta, int x, int y, int mouseX, int mouseY) {
        super.drawRightContent(matrices, delta, x, y, mouseX, mouseY);
        // code here
    }
}
'''
bole_screen_handler_template = '''package xienaoban.minecraft.bole.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import xienaoban.minecraft.bole.util.Keys;

public class Bole{#0}ScreenHandler<E extends {#0}> extends Bole{#2}ScreenHandler<E> {
    public static final ScreenHandlerType<Bole{#0}ScreenHandler<{#0}>> HANDLER = ScreenHandlerRegistry.registerSimple(
            new Identifier(Keys.NAMESPACE, "{#1}"), Bole{#0}ScreenHandler::new);

    public Bole{#0}ScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(HANDLER, syncId, playerInventory);
    }

    public Bole{#0}ScreenHandler(int syncId, PlayerInventory playerInventory, Entity entity) {
        this(HANDLER, syncId, playerInventory, entity);
    }

    public Bole{#0}ScreenHandler(ScreenHandlerType<?> handler, int syncId, PlayerInventory playerInventory) {
        this(handler, syncId, playerInventory, clientEntity());
    }

    public Bole{#0}ScreenHandler(ScreenHandlerType<?> handler, int syncId, PlayerInventory playerInventory, Entity entity) {
        super(handler, syncId, playerInventory, entity);
        // code here
    }

    @Override
    protected void initCustom() {
        // code here
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void clientTick(int ticks) {
        super.clientTick(ticks);
        // code here
    }

    @Override
    public void writeServerEntityToBuf(PacketByteBuf buf) {
        super.writeServerEntityToBuf(buf);
        // code here
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void readServerEntityFromBuf(PacketByteBuf buf) {
        super.readServerEntityFromBuf(buf);
        // code here
    }

    @Environment(EnvType.CLIENT)
    @Override
    protected void resetClientEntityServerProperties() {
        super.resetClientEntityServerProperties();
        // code here
    }
}
'''
bole_screen_register_template = 'ScreenRegistry.register(Bole{#0}ScreenHandler.HANDLER, Bole{#0}Screen<{#0}, Bole{#0}ScreenHandler<{#0}>>::new);'
bole_screen_name_template = 'Bole{#0}Screen'
bole_screen_handler_name_template = 'Bole{#0}ScreenHandler'
key1 = '{#0}'
key2 = '{#1}'
key3 = '{#2}'


print('Input entity class (e.g. "LivingEntity", "HorseEntity"):', end=' ')
clazz = input()
print('Input parent entity class (e.g. "Entity", "LivingEntity"):', end=' ')
parent_clazz = input()
print()

camel = clazz
assert camel[0].isupper()
under = re.sub(r'([a-z0-9])([A-Z])', r'\1_\2', camel).lower()
screen_class_name = bole_screen_name_template.replace(key1, camel)
screen_handler_class_name = bole_screen_handler_name_template.replace(key1, camel)
parent_screen_class_name = bole_screen_name_template.replace(key1, parent_clazz)
parent_screen_handler_class_name = bole_screen_handler_name_template.replace(key1, parent_clazz)
print('Your screen class will be:          ' + screen_class_name)
print('Your screen handler class will be:  ' + screen_handler_class_name)
print('Your channel name will be:          ' + under)
print('Your parent screen/handler will be: ' + parent_screen_class_name + " / " + parent_screen_handler_class_name)
print('Continue? [Y/n]: ', end='')
if input() == 'n': exit()
print()

os.makedirs('out', exist_ok=True)
with open('out/' + screen_class_name + '.java', 'w') as f:
    f.write(bole_screen_template.replace(key1, camel).replace(key3, parent_clazz))
with open('out/' + screen_handler_class_name + '.java', 'w') as f:
    f.write(bole_screen_handler_template.replace(key1, camel).replace(key2, under).replace(key3, parent_clazz))
print('Files has been exported. Check them in folder "./out".')
print()

print('Dont\'t forget to register them in method "onInitializeClient()":')
print()
print(' -> ' + bole_screen_register_template.replace(key1, camel))
print()