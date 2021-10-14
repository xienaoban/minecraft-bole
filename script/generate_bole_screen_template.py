import re, os

bole_screen_template = '''package xienaoban.minecraft.bole.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class Bole{#0}Screen extends AbstractBoleScreen<{#0}, Bole{#0}ScreenHandler> {
    public Bole{#0}Screen(Bole{#0}ScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        super.drawBackground(matrices, delta, mouseX, mouseY);
        drawEntityPlan(mouseX, mouseY);
    }
}
'''
bole_screen_handler_template = '''package xienaoban.minecraft.bole.screen;

import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import xienaoban.minecraft.bole.util.Keys;

public class Bole{#0}ScreenHandler extends AbstractBoleScreenHandler<{#0}> {
    public static final ScreenHandlerType<Bole{#0}ScreenHandler> HANDLER = ScreenHandlerRegistry.registerSimple(
            new Identifier(Keys.NAMESPACE, "{#1}"), Bole{#0}ScreenHandler::new);

    public Bole{#0}ScreenHandler(int syncId, PlayerInventory playerInventory) {
        super(HANDLER, syncId, playerInventory);
    }

    public Bole{#0}ScreenHandler(int syncId, PlayerInventory playerInventory, Entity entity) {
        super(HANDLER, syncId, playerInventory, entity);
    }

    @Override
    protected void init() {}
}
'''
bole_screen_register_template = 'ScreenRegistry.register(Bole{#0}ScreenHandler.HANDLER, Bole{#0}Screen::new);'
bole_screen_name_template = 'Bole{#0}Screen'
bole_screen_handler_name_template = 'Bole{#0}ScreenHandler'
key1 = '{#0}'
key2 = '{#1}'


print('Input entity class (e.g. "LivingEntity", "HorseEntity"):', end=' ')
clazz = input()
print()

camel = clazz
assert camel[0].isupper()
under = re.sub(r'([a-z0-9])([A-Z])', r'\1_\2', camel).lower()
screen_class_name = bole_screen_name_template.replace(key1, camel)
screen_handler_class_name = bole_screen_handler_name_template.replace(key1, camel)
print('Your screen class will be:         ' + screen_class_name)
print('Your screen handler class will be: ' + screen_handler_class_name)
print('Your channel name will be:         ' + under)
print('Continue? [Y/n]: ', end='')
if input() == 'n': exit()
print()

os.makedirs('out', exist_ok=True)
with open('out/' + screen_class_name + '.java', 'w') as f:
    f.write(bole_screen_template.replace(key1, camel))
with open('out/' + screen_handler_class_name + '.java', 'w') as f:
    f.write(bole_screen_handler_template.replace(key1, camel).replace(key2, under))
print('Files has been exported. Check them in folder "./out".')
print()

print('Dont\'t forget to register them in method "onInitializeClient()":')
print()
print(' -> ' + bole_screen_register_template.replace(key1, camel))
print()