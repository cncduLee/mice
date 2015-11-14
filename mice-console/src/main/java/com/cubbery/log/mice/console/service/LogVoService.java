/**
 * Copyright (c) 2015, cubbery.com. All rights reserved.
 */
package com.cubbery.log.mice.console.service;

import com.cubbery.log.mice.console.dao.EsSearchDao;
import com.cubbery.log.mice.console.vo.EsCondition;
import com.cubbery.log.mice.console.vo.LogVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <b>项目名</b>： mice-parent <br>
 * <b>包名称</b>： com.cubbery.log.mice.console.service <br>
 * <b>类名称</b>： LogVoService <br>
 * <b>类描述</b>： <br>
 * <b>创建人</b>： <a href="mailto:cubber@cubbery.com">cubbery</a> <br>
 * <b>修改人</b>： <br>
 * <b>创建时间</b>： 2015/11/15 <br>
 * <b>修改时间</b>： <br>
 * <b>修改备注</b>： <br>
 *
 * @version 1.0.0 <br>
 */
public interface LogVoService {
    List<LogVo> search(EsCondition condition);
}

@Service
class LogVoServiceImpl implements LogVoService {
    @Resource
    private EsSearchDao esSearchDao;

    @Override
    public List<LogVo> search(EsCondition condition) {
        return esSearchDao.search(condition,LogVo.class);
    }
}