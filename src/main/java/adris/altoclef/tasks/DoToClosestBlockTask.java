package adris.altoclef.tasks;


import adris.altoclef.AltoClef;
import adris.altoclef.tasksystem.Task;
import adris.altoclef.util.csharpisbetter.Util;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.function.Function;
import java.util.function.Supplier;


public class DoToClosestBlockTask extends AbstractDoToClosestObjectTask<BlockPos> {
    private final Block[] targetBlocks;
    private final Supplier<Vec3d> getOriginPos;
    private final Function<Vec3d, BlockPos> getClosest;
    private final Function<BlockPos, Task> getTargetTask;
    
    public DoToClosestBlockTask(AltoClef mod, Supplier<Vec3d> getOriginSupplier, Function<BlockPos, Task> getTargetTask, Block... blocks) {
        this(getOriginSupplier, getTargetTask, (origin) -> mod.getBlockTracker().getNearestTracking(origin, blocks), blocks);
    }
    
    public DoToClosestBlockTask(Supplier<Vec3d> getOriginSupplier, Function<BlockPos, Task> getTargetTask,
                                Function<Vec3d, BlockPos> getClosestBlock, Block... blocks) {
        getOriginPos = getOriginSupplier;
        this.getTargetTask = getTargetTask;
        targetBlocks = blocks;
        getClosest = getClosestBlock;
    }
    
    @Override
    protected Vec3d getPos(AltoClef mod, BlockPos obj) {
        return new Vec3d(obj.getX(), obj.getY(), obj.getZ());
    }
    
    @Override
    protected BlockPos getClosestTo(AltoClef mod, Vec3d pos) {
        return getClosest.apply(pos);
    }
    
    @Override
    protected Vec3d getOriginPos(AltoClef mod) {
        return getOriginPos.get();
    }
    
    @Override
    protected Task getGoalTask(BlockPos obj) {
        return getTargetTask.apply(obj);
    }
    
    @Override
    protected boolean isValid(AltoClef mod, BlockPos obj) {
        // Assume we're valid since we're in the same chunk.
        if (!mod.getChunkTracker().isChunkLoaded(obj)) return true;
        
        return mod.getBlockTracker().blockIsValid(obj, targetBlocks);
        /*
        BlockState state = mod.getWorld().getBlockState(obj);
        for (Block block : _targetBlocks) {
            if (state.getBlock().equals(block)) return true;
        }
        return false;
         */
    }
    
    @Override
    protected void onStart(AltoClef mod) {
        mod.getBlockTracker().trackBlock(targetBlocks);
    }
    
    @Override
    protected void onStop(AltoClef mod, Task interruptTask) {
        mod.getBlockTracker().stopTracking(targetBlocks);
    }
    
    @Override
    protected boolean isEqual(Task obj) {
        if (obj instanceof DoToClosestBlockTask) {
            DoToClosestBlockTask task = (DoToClosestBlockTask) obj;
            return Util.arraysEqual(task.targetBlocks, targetBlocks);
        }
        return false;
    }
    
    @Override
    protected String toDebugString() {
        return "Doing something to closest block...";
    }
}