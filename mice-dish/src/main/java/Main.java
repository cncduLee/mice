/**
 * Copyright (c) 2015, cubbery.com. All rights reserved.
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * <b>项目名</b>： mice-parent <br>
 * <b>包名称</b>： PACKAGE_NAME <br>
 * <b>类名称</b>： Main <br>
 * <b>类描述</b>： <br>
 * <b>创建人</b>： <a href="mailto:cubber@cubbery.com">cubbery</a> <br>
 * <b>修改人</b>： <br>
 * <b>创建时间</b>： 2015/11/15 <br>
 * <b>修改时间</b>： <br>
 * <b>修改备注</b>： <br>
 *
 * @version 1.0.0 <br>
 */
public class Main {
    static Logger logger = LoggerFactory.getLogger(Main.class);
    public static void main(String[] args) throws InterruptedException {
        while (!Thread.interrupted()) {
            UUID uuid = UUID.randomUUID();
            for(int a = 0;a<100;a++) {
                logger.info("This is A Basic Logger Collector Test! TAG = {}, UUID = {}",a,uuid);
            }
            Thread.sleep(1000L);
        }
    }
}
