package vn.abs.erp.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(basePackages = {
        "vn.abs.erp.notification11.mapper",
        "vn.abs.erp.notification.mapper",
        "vn.abs.erp.msg.category.mapper",
})
public class AbsConfigurations {
}
