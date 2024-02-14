package com.github.enr.resources;

public record PrioritizableResourceLocation(int priority, ResourceLocation location) implements Prioritizable {

}
