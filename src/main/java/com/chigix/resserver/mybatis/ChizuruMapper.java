package com.chigix.resserver.mybatis;

import com.chigix.resserver.mybatis.dto.ApplicationContextDto;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public interface ChizuruMapper {

    public static final Function<List<Map<String, String>>, Map<String, String>> SETTINGS_MAP = (List<Map<String, String>> t) -> {
        Map<String, String> result = new HashMap<>();
        t.forEach((map) -> {
            result.put(map.get("key"), map.get("value"));
        });
        return result;
    };

    List<Map<String, String>> selectChizuruSettings();

    int updateChizuruSettings(ApplicationContextDto dto);

    int saveNodeId(ApplicationContextDto dto);

    int saveMaxChunkSize(ApplicationContextDto dto);

    int saveCreatingDate(ApplicationContextDto dto);
}
