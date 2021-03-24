package me.aiglez.gangs.exceptions;

public class DependencyNotFoundException extends RuntimeException {

    public DependencyNotFoundException(final String dependency) {
        super("Couldn't find the dependency with name " + dependency + ", disabling the plugin...");
    }

}
