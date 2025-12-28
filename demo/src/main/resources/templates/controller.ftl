package ${table.basePackage}.controller;

import ${table.basePackage}.entity.${table.className};
import ${table.basePackage}.service.I${table.className}Service;
import com.fxly.demo.system.global.HttpResultEnum;
import com.fxly.demo.system.global.HttpResult;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import io.swagger.v3.oas.annotations.Operation;

import java.util.List;

/**
* ${table.tableComment} 控制器
* @author ${table.author}
*/
@RestController
@RequestMapping("/${table.tableName}")
public class ${table.className}Controller{

    @Resource
    private I${table.className}Service ${table.className?uncap_first}Service;

    @Operation(summary = "查询")
    @GetMapping("/getById")
    public HttpResult getById(@RequestParam("id") Long id) {
        return HttpResult.success(${table.className?uncap_first}Service.getById(id));
    }

    @Operation(summary = "列表查询")
    @GetMapping("/list")
    public HttpResult list(${table.className} entity) {
        List<${table.className}> list = ${table.className?uncap_first}Service.list();
        return HttpResult.success(list) ;
    }

    @Operation(summary = "新增")
    @PostMapping("/save")
    public HttpResult save(@RequestBody ${table.className} ${table.className?uncap_first}) {
        return HttpResult.success(${table.className?uncap_first}Service.save(${table.className?uncap_first}));
    }

    @Operation(summary = "修改")
    @PostMapping("/update")
    public HttpResult update(@RequestBody ${table.className} ${table.className?uncap_first}) {
        return HttpResult.success(${table.className?uncap_first}Service.updateById(${table.className?uncap_first}));
    }

    @Operation(summary = "删除")
    @PostMapping("/delete")
    public HttpResult delete(@RequestParam("id") Long id) {
        boolean b = ${table.className?uncap_first}Service.removeById(id);
        return b ? HttpResult.setResult(HttpResultEnum.DELETE_SUCCESS)
         : HttpResult.setResult(HttpResultEnum.DELETE_ERROR);
    }
}