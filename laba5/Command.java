package Interfaces;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)

@Target(ElementType.METHOD)
public @interface Command
{
    String name();
    String description();
}