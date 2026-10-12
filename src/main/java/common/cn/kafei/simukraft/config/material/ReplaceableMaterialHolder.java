package common.cn.kafei.simukraft.config.material;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;

import java.util.*;

public class ReplaceableMaterialHolder {
    public final String holderName;
    public final Map<Integer, Set<ResourceKey<Block>>> replaceableBlocks;
    public final Set<ReplaceableMaterialHolder> replaceableHolders;
    private int subReplaceableCount = 1;
    // id = 0: every element not be replaceable together,only replaceable by parents holder
    // id > 0: elements in same id can be replaceable together

    public ReplaceableMaterialHolder(String holderName) {
        this.holderName = holderName;
        this.replaceableBlocks = new HashMap<>();
        this.replaceableHolders = new HashSet<>();
    }

    @SafeVarargs
    public final void addUnreplaceableBlock(ResourceKey<Block>... blocks) {
        addReplaceableBlock(0, blocks);
    }

    @SafeVarargs
    // 如果你需要注册多组独立运作的可替换组，请多次注册，他们可以拥有共同上游方块
    public final int addReplaceableBlock(ResourceKey<Block>... blocks) {
        return addReplaceableBlock(Arrays.asList(blocks));
    }

    @SafeVarargs
    public final void addReplaceableBlock(int subId, ResourceKey<Block>... blocks) {
        addReplaceableBlock(subId, Arrays.asList(blocks));
    }

    public final void addUnreplaceableBlock(Collection<ResourceKey<Block>> blocks) {
        addReplaceableBlock(0, blocks);
    }

    public final int addReplaceableBlock(Collection<ResourceKey<Block>> blocks) {
        addReplaceableBlock(this.subReplaceableCount, blocks);
        this.subReplaceableCount++;
        return this.subReplaceableCount;
    }

    public final void addReplaceableBlock(int subId, Collection<ResourceKey<Block>> blocks) {
        Set<ResourceKey<Block>> blockSet = this.replaceableBlocks.computeIfAbsent(subId, _ -> new HashSet<>());
        blockSet.addAll(blocks);
    }

    /**
     * 读档后对齐自动分组编号，避免下一次追加覆盖已有 id。
     */
    public void syncSubReplaceableCount() {
        int next = 1;
        for (Integer id : this.replaceableBlocks.keySet()) {
            if (id != null && id >= next) {
                next = id + 1;
            }
        }
        this.subReplaceableCount = next;
    }

    public void addReplaceableHolder(ReplaceableMaterialHolder replaceableHolder) {
        if (this == replaceableHolder) {
            LogUtils.getLogger().warn("Unable to bind replaceable holder to itself");
            return;
        }
        if (replaceableHolder.getAllReplaceableHolders().contains(this)) {
            LogUtils.getLogger().warn("Unable to loop bind replaceable holder to its descendant");
            return;
        }
        this.replaceableHolders.add(replaceableHolder);
    }

    // 传入null时为无条件添加分组内的全部id
    public Set<ResourceKey<Block>> getAllReplaceableBlocks(ResourceKey<Block> block) {
        Set<ResourceKey<Block>> set = new HashSet<>();
        for (Map.Entry<Integer, Set<ResourceKey<Block>>> entry : replaceableBlocks.entrySet()) {
            if (block == null || entry.getKey() != 0 && entry.getValue().contains(block)) {
                set.addAll(entry.getValue());
            }
        }
        for (ReplaceableMaterialHolder holder : replaceableHolders) {
            set.addAll(holder.getAllReplaceableBlocks(null));
        }
        return set;
    }

    private Set<ReplaceableMaterialHolder> getAllReplaceableHolders() {
        Set<ReplaceableMaterialHolder> set = new HashSet<>(this.replaceableHolders);
        for (ReplaceableMaterialHolder holder : this.replaceableHolders) {
            set.addAll(holder.getAllReplaceableHolders());
        }
        return set;
    }
}
