package com.ulyp.storage.search;

import com.ulyp.core.Method;
import com.ulyp.core.EnterMethodCall;
import com.ulyp.core.ExitMethodCall;
import com.ulyp.core.Type;
import com.ulyp.core.repository.ReadableRepository;

public interface SearchQuery {

    boolean matches(
            EnterMethodCall enterMethodCall,
            ReadableRepository<Integer, Type> types,
            ReadableRepository<Integer, Method> methods
    );

    boolean matches(
            ExitMethodCall exitMethodCall,
            ReadableRepository<Integer, Type> typeResolver,
            ReadableRepository<Integer, Method> methods
    );
}
