package com.chigix.resserver.mybatis.dto;

import com.chigix.resserver.ApplicationContext;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class ApplicationContextDto {

    private final ApplicationContext bean;

    public ApplicationContextDto(ApplicationContext bean) {
        this.bean = bean;
    }

    public ApplicationContext getBean() {
        return bean;
    }

    public String getCreationDate() {
        return bean.getCreationDate().toString();
    }

}
