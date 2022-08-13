package com.myf;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.fill.Column;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.*;

@SpringBootTest
class SimpleFunctionsApplicationTests {

    ExpressionParser parser = new SpelExpressionParser();
    TemplateParserContext parserContext = new TemplateParserContext();
    String redisNameSpace = "";

    @Test
    void contextLoads() {

        List<String> strings = new ArrayList<>();
        strings.add("a");
        strings.add("b");
        Apple apple = new Apple();
        apple.setArray(strings);

        ExpressionParser parser = new SpelExpressionParser();
        EvaluationContext context = new StandardEvaluationContext();
        Expression expression = parser.parseExpression("redisson-#{#apple.getArray()}",parserContext);
        context.setVariable("apple",apple);
        Object value = expression.getValue(context);
        if (value instanceof List<?>){
            List<String> arr = (List<String>) value;
            for (String s : arr) {
                System.out.println(s);
            }
        }
        System.out.println(value);


    }

    @Data
    class Apple{
        List<String> array;
    }

    @Test
    void generateTable(){
        FastAutoGenerator.create("jdbc:mysql://localhost:3306/simple_functions?characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&useLegacyDatetimeCode=false&serverTimezone=GMT%2B8","root","110110110")
                // 全局配置
                .globalConfig((scanner, builder) -> builder.author("myf").outputDir("src/main/java/"))
                // 包配置
                .packageConfig((scanner, builder) -> builder.parent("com.myf"))
                // 策略配置
                .strategyConfig((scanner, builder) -> builder.addInclude(getTables("hello_word"))
                        .controllerBuilder().enableRestStyle().enableHyphenStyle()
                        .entityBuilder().enableLombok().addTableFills(
                                new Column("create_time", FieldFill.INSERT)
                        ).build())
                /*
                    模板引擎配置，默认 Velocity 可选模板引擎 Beetl 或 Freemarker
                   .templateEngine(new BeetlTemplateEngine())
                   .templateEngine(new FreemarkerTemplateEngine())
                 */
                .execute();



    }

    // 处理 all 情况
    protected static List<String> getTables(String tables) {
        return "all".equals(tables) ? Collections.emptyList() : Arrays.asList(tables.split(","));
    }

}
