package org.forsstudio.nbatestsuit.domain;

import java.io.Serial;
import java.io.Serializable;

public abstract class AbstractEntity<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public abstract T getId();
}
