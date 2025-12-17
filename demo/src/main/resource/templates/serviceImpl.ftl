package ${table.basePackage}.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ${table.basePackage}.entity.${table.className};
import ${table.basePackage}.mapper.${table.className}Mapper;
import ${table.basePackage}.service.I${table.className}Service;
import org.springframework.stereotype.Service;

/**
* ${table.tableComment} Service实现类
* @author ${table.author}
*/

@Service
public class ${table.className}ServiceImpl extends ServiceImpl<${table.className}Mapper, ${table.className}> implements I${table.className}Service {

}