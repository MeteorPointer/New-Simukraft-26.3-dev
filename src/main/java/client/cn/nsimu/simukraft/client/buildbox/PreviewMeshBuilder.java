package client.cn.nsimu.simukraft.client.buildbox;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class PreviewMeshBuilder {
    private PreviewMeshBuilder() {
    }

    public static PreviewMesh build(List<PreviewBlockData> allBlocks) {
        return build(allBlocks, Map.of());
    }

    /**
     * build：26.3 把方块提交交给几何收集器，occluders 只在调用方做邻面剔除。
     */
    public static PreviewMesh build(List<PreviewBlockData> allBlocks, Map<Long, BlockState> occluders) {
        if (allBlocks == null || allBlocks.isEmpty()) {
            return PreviewMesh.EMPTY;
        }
        List<PreviewBlockData> blocks = new ArrayList<>(allBlocks.size());
        for (PreviewBlockData block : allBlocks) {
            BlockState state = block.state();
            if (state == null || state.isAir()) {
                continue;
            }
            blocks.add(block);
        }
        if (blocks.isEmpty()) {
            return PreviewMesh.EMPTY;
        }
        return new PreviewMesh(findMeshOrigin(blocks), blocks);
    }

    private static BlockPos findMeshOrigin(List<PreviewBlockData> allBlocks) {
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int minZ = Integer.MAX_VALUE;
        for (PreviewBlockData block : allBlocks) {
            BlockPos pos = block.pos();
            minX = Math.min(minX, pos.getX());
            minY = Math.min(minY, pos.getY());
            minZ = Math.min(minZ, pos.getZ());
        }
        if (minX == Integer.MAX_VALUE) {
            return BlockPos.ZERO;
        }
        return new BlockPos(minX, minY, minZ);
    }
}
