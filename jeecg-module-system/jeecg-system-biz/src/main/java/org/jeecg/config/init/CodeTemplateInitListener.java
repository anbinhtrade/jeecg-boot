package org.jeecg.config.init;

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Automatically initialize code generator templates
 * <p>
 * Fixed the issue that the code generator template needs to be manually configured for JAR release
 * @author zhang
 */
@Slf4j
@Component
public class CodeTemplateInitListener implements ApplicationListener<ApplicationReadyEvent> {

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try {
            log.info(" Init Code Generate Template [ If the GAR is booted in the environment, copy the template to the config directory ] ");
            this.initJarConfigCodeGeneratorTemplate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ::The JAR package is started in the startup mode::
     * Initialize the code generator template file
     */
    private void initJarConfigCodeGeneratorTemplate() throws Exception {
        //1.获取jar同级下的config路径
        String configPath = System.getProperty("user.dir") + File.separator + "config" + File.separator;
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath*:jeecg/code-template-online/**/*");
        for (Resource re : resources) {
            URL url = re.getURL();
            String filepath = url.getPath();
            //System.out.println("native url= " + filepath);
            filepath = java.net.URLDecoder.decode(filepath, "utf-8");
            //System.out.println("decode url= " + filepath);

            //2.在config下，创建jeecg/code-template-online/*模板
            String createFilePath = configPath + filepath.substring(filepath.indexOf("jeecg/code-template-online"));

            // Templates are not generated in non-JAR mode
            // No directory is generated, only specific template files are generated
            if (!filepath.contains(".jar!/BOOT-INF/lib/") || !createFilePath.contains(".")) {
                continue;
            }
            if (!FileUtil.exist(createFilePath)) {
                log.info("create file codeTemplate = " + createFilePath);
                FileUtil.writeString(IOUtils.toString(url, StandardCharsets.UTF_8), createFilePath, "UTF-8");
            }
        }
    }
}
