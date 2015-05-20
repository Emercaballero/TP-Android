package com.teamdc.stephendiniz.autoaway.classes;

/**
 * Created by sscotti on 5/19/15.
 */
public interface RegisterDeserializer<T> {

    T deserialize(String line);
}
