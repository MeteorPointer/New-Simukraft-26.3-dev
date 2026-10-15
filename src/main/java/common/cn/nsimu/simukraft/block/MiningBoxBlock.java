package common.cn.nsimu.simukraft.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * 挖矿盒：硬度、正确工具掉落和音效由注册属性设成石头。
 */
public final class MiningBoxBlock extends Block {
    public MiningBoxBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }
}
