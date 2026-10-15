package client.cn.nsimu.simukraft.client.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * ConfigButtonHandler：在主菜单 Mod 按钮左侧和暂停菜单统计信息按钮右侧注入配置入口按钮。
 */

public final class ConfigButtonHandler {

    private static final int BUTTON_SIZE = 20;
    private static final int BUTTON_SPACING = 5;

    /**
     * onScreenInit：主菜单 / 暂停菜单初始化后注入按钮。
     */
    @SubscribeEvent
    public void onScreenInit(ScreenEvent.Init.Post event) {
        if (event.getScreen() instanceof TitleScreen titleScreen) {
            injectTitleScreenButton(event, titleScreen);
        } else if (event.getScreen() instanceof PauseScreen pauseScreen) {
            injectPauseScreenButton(event, pauseScreen);
        }
    }

    /**
     * injectTitleScreenButton：在 Mod 按钮左侧添加配置按钮。
     */
    private void injectTitleScreenButton(ScreenEvent.Init.Post event, TitleScreen screen) {
        Button modButton = findButtonByKey(screen, "fml.menu.mods");
        if (modButton == null) {
            modButton = findModButton(screen);
        }
        if (modButton == null) {
            return;
        }
        addBeside(event, screen, modButton, true, true);
    }

    /**
     * injectPauseScreenButton：在统计信息按钮右侧添加配置按钮。
     * 暂停菜单这一排原版已经居中，只补按钮，不再整排二次居中。
     */
    private void injectPauseScreenButton(ScreenEvent.Init.Post event, PauseScreen screen) {
        Button statsButton = findStatsButton(screen);
        if (statsButton == null) {
            return;
        }
        addBeside(event, screen, statsButton, false, false);
    }

    /**
     * addBeside: 把标志按钮放到锚点按钮旁。recenter 为真时再把整排挪回屏幕水平中心。
     */
    private void addBeside(ScreenEvent.Init.Post event, Screen screen, Button anchor, boolean left, boolean recenter) {
        List<Button> row = buttonsOnSameRow(screen, anchor);
        int buttonY = anchor.getY() + (anchor.getHeight() - BUTTON_SIZE) / 2;
        int buttonX = left
                ? leftmost(row) - BUTTON_SIZE - BUTTON_SPACING
                : rightmost(row) + BUTTON_SPACING;
        AnimatedIconButton btn = new AnimatedIconButton(buttonX, buttonY, BUTTON_SIZE, BUTTON_SIZE,
                b -> Minecraft.getInstance().gui.setScreen(SimuKraftConfigSelectionScreen.create(event.getScreen())));
        btn.setTooltip(Tooltip.create(nn(Component.translatable("gui.simukraft.config_button.title"))));
        event.addListener(nn(btn));
        if (!recenter) {
            return;
        }
        row.add(btn);
        centerRow(screen, row);
    }

    private static List<Button> buttonsOnSameRow(Screen screen, Button anchor) {
        List<Button> row = new ArrayList<>();
        int top = anchor.getY();
        int bottom = anchor.getY() + anchor.getHeight();
        for (GuiEventListener listener : screen.children()) {
            if (!(listener instanceof Button button)) {
                continue;
            }
            int mid = button.getY() + button.getHeight() / 2;
            if (mid >= top && mid < bottom) {
                row.add(button);
            }
        }
        if (!row.contains(anchor)) {
            row.add(anchor);
        }
        return row;
    }

    private static void centerRow(Screen screen, List<Button> row) {
        int minX = leftmost(row);
        int maxX = rightmost(row);
        int shift = screen.width / 2 - (minX + maxX) / 2;
        if (shift == 0) {
            return;
        }
        for (Button button : row) {
            button.setX(button.getX() + shift);
        }
    }

    private static int leftmost(List<Button> row) {
        int minX = Integer.MAX_VALUE;
        for (Button button : row) {
            minX = Math.min(minX, button.getX());
        }
        return minX;
    }

    private static int rightmost(List<Button> row) {
        int maxX = Integer.MIN_VALUE;
        for (Button button : row) {
            maxX = Math.max(maxX, button.getX() + button.getWidth());
        }
        return maxX;
    }

    @Nullable
    private Button findButtonByKey(Screen screen, String key) {
        for (GuiEventListener listener : screen.children()) {
            if (listener instanceof Button button
                    && button.getMessage().getContents() instanceof TranslatableContents contents
                    && key.equals(contents.getKey())) {
                return button;
            }
        }
        return null;
    }

    /**
     * findModButton：在主菜单查找 Mods 按钮（位于屏幕左半侧，Y ≈ height/4+96）。
     */
    @Nullable
    private Button findModButton(TitleScreen screen) {
        List<? extends GuiEventListener> children = screen.children();
        for (GuiEventListener listener : children) {
            if (listener instanceof Button button && button.getX() < screen.width / 2) {
                if (Math.abs(button.getY() - (screen.height / 4 + 48 + 48)) < 10) {
                    return button;
                }
            }
        }
        // 退化：返回左半侧最后一个按钮
        Button fallback = null;
        for (GuiEventListener listener : children) {
            if (listener instanceof Button button && button.getX() < screen.width / 2) {
                fallback = button;
            }
        }
        return fallback;
    }

    /**
     * findStatsButton：通过原版翻译键查找暂停菜单的统计信息按钮。
     */
    @Nullable
    private Button findStatsButton(PauseScreen screen) {
        return findButtonByKey(screen, "gui.stats");
    }

    private static <T> T nn(T value) {
        return Objects.requireNonNull(value);
    }
}
