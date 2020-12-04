package com.possible_triangle.brazier.config;

import net.minecraft.predicate.entity.DistancePredicate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.function.BiFunction;

public class DistanceHandler {

    @SuppressWarnings("unused")
    public enum Type {

        SPHERE((a, b) -> b.getSquaredDistance(a, true)),
        CYLINDER((a, b) -> {
            Vec3d c = new Vec3d(a.getX(), b.getY(), a.getZ());
            return b.getSquaredDistance(c, true);
        });

        private final BiFunction<Vec3d, BlockPos, Double> calc;

        Type(BiFunction<Vec3d, BlockPos, Double> calc) {
            this.calc = calc;
        }

    }

    public static double getDistance(Vec3d from, BlockPos to) {
        //return BrazierConfig.SERVER.DISTANCE_CALC.get().calc.apply(from, to);
        // TODO use config value
        return Type.CYLINDER.calc.apply(from, to);
    }

    public static double getDistance(BlockPos from, BlockPos to) {
        return getDistance(new Vec3d(from.getX(), from.getY(), from.getZ()), to);
    }

}
