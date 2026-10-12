package common.cn.kafei.simukraft.citizen;

import common.cn.kafei.simukraft.MinecraftTestBootstrap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


class CitizenFoodConsumptionServiceTest {
    @BeforeAll
    static void bootstrapMinecraft() {
        MinecraftTestBootstrap.ensure();
    }

    @Test
    void identifiesFoodPoisoningItems() {
        assertTrue(CitizenFoodConsumptionService.isFoodPoisoningItem(new ItemStack(Items.SPIDER_EYE)));
        assertTrue(CitizenFoodConsumptionService.isFoodPoisoningItem(new ItemStack(Items.ROTTEN_FLESH)));
        assertTrue(CitizenFoodConsumptionService.isFoodPoisoningItem(new ItemStack(Items.PUFFERFISH)));
    }

    @Test
    void doesNotMarkSafeFoodAsPoisonous() {
        assertFalse(CitizenFoodConsumptionService.isFoodPoisoningItem(new ItemStack(Items.BREAD)));
    }
}
