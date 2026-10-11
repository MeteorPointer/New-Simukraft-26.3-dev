package common.cn.kafei.simukraft.block;

import common.cn.kafei.simukraft.SimuKraft;
import common.cn.kafei.simukraft.network.geology.GeologicalSurveyHintService;
import common.cn.kafei.simukraft.registry.ModSoundEvents;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Locale;

/**
 * NSUK 灯块：右键播放灯块语音，并用地质锤文本框逐字显示台词。
 */
public final class LightBlock extends Block {
    static final Identifier TALKING_LIGHT_ADVANCEMENT =
            Identifier.fromNamespaceAndPath(SimuKraft.MOD_ID, "story/talking_light");
    static final String TALKING_LIGHT_CRITERION = "use_light";

    public LightBlock(Properties properties) {
        super(properties.mapColor(MapColor.METAL)
                .strength(1.0F)
                .sound(SoundType.GLASS)
                .lightLevel(state -> 15));
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level instanceof ServerLevel && player instanceof ServerPlayer serverPlayer) {
            level.playSound(null, pos, ModSoundEvents.LIGHT_USE.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
            GeologicalSurveyHintService.sendTypewriter(
                    serverPlayer,
                    Component.translatable(LightBlockSpeech.KEY),
                    LightBlockSpeech.SPEECH_MILLIS);
            grantTalkingLightAdvancement(serverPlayer);
        }
        return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
    }

    static void grantTalkingLightAdvancement(ServerPlayer player) {
        if (player.level().getServer() == null) {
            return;
        }
        AdvancementHolder advancement = player.level().getServer().getAdvancements().get(TALKING_LIGHT_ADVANCEMENT);
        if (advancement == null) {
            return;
        }
        if (player.getAdvancements().getOrStartProgress(advancement).isDone()) {
            return;
        }
        player.getAdvancements().award(advancement, TALKING_LIGHT_CRITERION);
    }

    /**
     * LightBlockSpeech: 灯块右键台词，按玩家语言选中文或英文。
     */
    public static final class LightBlockSpeech {
        public static final String KEY = "message.simukraft.light_block.speech";
        public static final String KEY_EN = "message.simukraft.light_block.speech.en";
        public static final String CHINESE = "你好，我是灯箱。我给 Minecraft 世界带来光明。谢谢你点击我。从这里看出去风景不错。谢谢你把我放在这儿。祝你今天愉快。感谢收听。";
        public static final String ENGLISH = "Hello, I am a lightbox. I bring light into the Minecraft world. Thanks for clicking me. There's a nice view from here. Thanks for placing me here. Have a nice day. Thank you for watching.";
        public static final int SPEECH_MILLIS = 13_400;

        private LightBlockSpeech() {
        }

        public static boolean isChineseLanguage(String languageCode) {
            return languageCode != null && languageCode.toLowerCase(Locale.ROOT).startsWith("zh");
        }

        public static String textForLanguage(String languageCode) {
            return isChineseLanguage(languageCode) ? CHINESE : ENGLISH;
        }
    }
}
