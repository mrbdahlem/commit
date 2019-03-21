package run.mycode.commit.persistence.util;

import java.io.Serializable;

public interface Identifiable<T extends Serializable> {
    T getId();
}