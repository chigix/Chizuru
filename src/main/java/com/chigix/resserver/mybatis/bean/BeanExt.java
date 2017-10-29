package com.chigix.resserver.mybatis.bean;

import com.chigix.resserver.domain.Lifecycle;
import com.chigix.resserver.mybatis.EntityManagerImpl;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public interface BeanExt {

    Lifecycle getEntityStatus(EntityManagerImpl em);

}
