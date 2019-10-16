package com.aegis.demo.controller;

import com.aegis.demo.pojo.SimpleResponse;
import com.aegis.demo.pojo.Student;
import com.aegis.demo.repository.StudentRepository;
import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.persistence.criteria.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author 李成超
 * @date 2019/10/9 17:07
 * @description TODO
 **/
@Api(tags = "学生接口")
@RestController
@RequestMapping("student")
public class StudentController {
    private static final Logger logger = LoggerFactory.getLogger(StudentController.class);
    @Resource
    private StudentRepository studentRepository;

    @ApiOperation(value = "查询学生列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page",defaultValue = "0",dataType = "int",paramType = "query",value = "分页页数",example = "0"),
            @ApiImplicitParam(name = "size",defaultValue = "5",dataType = "int",paramType = "query",value = "每页条数",example = "5")
    })
    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public SimpleResponse<List<Student>> list(@RequestParam(value = "page",defaultValue = "0") int page, @RequestParam(value = "size",defaultValue = "5") int size){
        SimpleResponse<List<Student>> simpleResponse = new SimpleResponse<>();
        try {
            Sort sort = new Sort(Sort.Direction.DESC,"sId");
            Pageable pageable = new PageRequest(page,size,sort);
            Page<Student> studentPage = studentRepository.findList(pageable);
            if (studentPage.getContent()!=null || studentPage.getContent().size()>0){
                simpleResponse.setData(studentPage.getContent());
            }
            simpleResponse.setCode("0");
            simpleResponse.setMsg("success");
            logger.info("查询学生列表成功"+studentPage.getContent());
        } catch (Exception e){
            logger.error("查询学生列表失败",e);
            simpleResponse.setCode("99");
            simpleResponse.setMsg("查询学生列表异常,"+e.getMessage());
        }
        return simpleResponse;
    }
    @ApiOperation(value = "通过学生ID查询学生")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sId",paramType = "path",value = "学生ID",required = true)
    })
    @RequestMapping(value = "/get/{sId}",method = RequestMethod.GET)
    public SimpleResponse<Student> get(@PathVariable String sId){
        SimpleResponse<Student> simpleResponse = new SimpleResponse<>();
        try {
            Optional<Student> bysId = studentRepository.findBysId(sId);
            if (bysId.isPresent()){
                Student student = bysId.get();
                simpleResponse.setData(student);
            }
            simpleResponse.setCode("0");
            simpleResponse.setMsg("success");
        } catch (Exception e){
            logger.error("查询学生失败",e);
            simpleResponse.setCode("99");
            simpleResponse.setMsg("查询学生异常,"+e.getMessage());
        }
        return simpleResponse;
    }

    @ApiOperation(value = "通过姓名模糊查询学生列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page",defaultValue = "0",dataType = "int",paramType = "query",value = "分页页数",required = false,example = "0"),
            @ApiImplicitParam(name = "size",defaultValue = "5",dataType = "int",paramType = "query",value = "每页条数",required = false,example = "5"),
            @ApiImplicitParam(name = "sName",dataType = "String",paramType = "query",value = "学生姓名",required = true)
    })
    @RequestMapping(value = "getByName",method = RequestMethod.GET)
    public SimpleResponse<List<Student>> getByName(@RequestParam(value = "sName",required = false) String sName,@RequestParam(value = "page",defaultValue = "0") Integer page, @RequestParam(value = "size",defaultValue = "5") Integer size){
        SimpleResponse<List<Student>> simpleResponse = new SimpleResponse<>();

        try {
            if (StringUtils.isBlank(sName)){
                throw new Exception("学生姓名sName不能为空");
            }
            Sort sort = new Sort(Sort.Direction.DESC,"sId");
            Pageable pageable = new PageRequest(page,size,sort);
            // 模糊查询
            Page<Student> studentPage = studentRepository.findBysNameLike("%"+sName+"%",pageable);
            if (studentPage.getContent()!=null || studentPage.getContent().size()>0){
                simpleResponse.setData(studentPage.getContent());
            }
            simpleResponse.setCode("0");
            simpleResponse.setMsg("success");
        } catch (Exception e){
            logger.error("根据姓名模糊查询学生失败",e);
            simpleResponse.setCode("99");
            simpleResponse.setMsg("根据姓名模糊查询学生异常,"+e.getMessage());
        }
        return simpleResponse;
    }

    @ApiOperation(value = "添加学生")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sName",dataType = "String",paramType = "query",value = "学生姓名",required = true),
            @ApiImplicitParam(name = "sBirth",dataType = "String",paramType = "query",value = "学生生日",required = true,example = "1999-01-01"),
            @ApiImplicitParam(name = "sSex",dataType = "String",paramType = "query",value = "性别",required = true)
    })
    @RequestMapping(value = "/add",method = RequestMethod.PUT)
    public SimpleResponse add(@RequestParam(value = "sName") String sName, @RequestParam(value = "sBirth") String sBirth,@RequestParam(value = "sSex") String sSex){
        SimpleResponse simpleResponse = new SimpleResponse();
        try {
            Student student = new Student();
            student.setSBirth(sBirth).setSSex(sSex).setSName(sName);
            int num = new Random().nextInt(899)+100;
            student.setSId(new SimpleDateFormat("yyMMddHHmmss").format(new Date())+num);

            studentRepository.save(student);
            simpleResponse.setCode("0");
            simpleResponse.setMsg("success");
        } catch (Exception e){
            logger.error("添加学生失败",e);
            simpleResponse.setCode("99");
            simpleResponse.setMsg("添加学生异常,"+e.getMessage());
        }
        return simpleResponse;
    }
    @ApiOperation(value = "通过ID删除学生")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sId",paramType = "path",value = "学生ID",required = true,example = "01")
    })
    @RequestMapping(value = "/delete/{sId}",method = RequestMethod.DELETE)
    public SimpleResponse delete(@PathVariable(value = "sId") String sId){
        SimpleResponse simpleResponse = new SimpleResponse();
        try {
            studentRepository.deleteBysId(sId);
            simpleResponse.setCode("0");
            simpleResponse.setMsg("success");
        }catch (Exception e){
            logger.error("删除学生失败",e);
            simpleResponse.setCode("99");
            simpleResponse.setMsg("删除学生异常,"+e.getMessage());
        }
        return simpleResponse;
    }

    @ApiOperation(value = "更新学生")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sId",dataType = "String",paramType = "query",value = "学生ID",required = true),
            @ApiImplicitParam(name = "sName",dataType = "String",paramType = "query",value = "学生姓名",required = true),
            @ApiImplicitParam(name = "sBirth",dataType = "String",paramType = "query",value = "学生生日",required = true,example = "1999-01-01"),
            @ApiImplicitParam(name = "sSex",dataType = "String",paramType = "query",value = "性别",required = true)
    })
    @RequestMapping(value = "/update",method = RequestMethod.PUT)
    public SimpleResponse update(@RequestParam(value = "sId",required = false) String sId,@RequestParam(value = "sName",required = false) String sName, @RequestParam(value = "sBirth",required = false) String sBirth,@RequestParam(value = "sSex",required = false) String sSex){
        SimpleResponse simpleResponse = new SimpleResponse();
        try {
            if (StringUtils.isBlank(sId)){
                throw new Exception("学生姓名sId不能为空");
            }
            Student student = new Student();
            student.setSBirth(sBirth).setSSex(sSex).setSName(sName).setSId(sId);

            studentRepository.save(student);
            simpleResponse.setCode("0");
            simpleResponse.setMsg("success");
        } catch (Exception e){
            logger.error("更新学生失败",e);
            simpleResponse.setCode("99");
            simpleResponse.setMsg("更新学生异常,"+e.getMessage());
        }
        return simpleResponse;
    }
    @ApiOperation(value = "通过学生性别、姓名查询（JpaSpecificationExecutor方法）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sSex",dataType = "String",paramType = "query",value = "学生性别",required = true),
            @ApiImplicitParam(name = "sName",dataType = "String",paramType = "query",value = "学生名称(模糊匹配)",required = true),
            @ApiImplicitParam(name = "page",dataType = "int",paramType = "query",value = "分页页数",example = "0"),
            @ApiImplicitParam(name = "size",dataType = "int",paramType = "query",value = "每页条数",example = "5")
    })
    @RequestMapping(value = "/getbySex",method = RequestMethod.GET)
    public SimpleResponse<List<Student>> getbySex(@RequestParam(value = "sSex",required = true) String sSex,@RequestParam(value = "sName",required = true) String sName,@RequestParam(value = "page",defaultValue = "0") Integer page, @RequestParam(value = "size",defaultValue = "5") Integer size){
        SimpleResponse<List<Student>> simpleResponse = new SimpleResponse<>();
        try {
            Sort sort = new Sort(Sort.Direction.DESC,"sId");
            Pageable pageable = new PageRequest(page,size,sort);
            Page<Student> students = studentRepository.findAll((root, criteriaQuery, criteriaBuilder) -> {
                Path<Object> sSexPath = root.get("sSex");
                Path<Object> sNamePath = root.get("sName");
                Predicate predicate = criteriaBuilder.and(criteriaBuilder.equal(sSexPath, sSex),criteriaBuilder.like(sNamePath.as(String.class),"%"+sName+"%"));
                return predicate;
            },pageable);

            simpleResponse.setData(students.getContent());

            simpleResponse.setCode("0");
            simpleResponse.setMsg("success");
        } catch (Exception e){
            logger.error("查询学生失败",e);
            simpleResponse.setCode("99");
            simpleResponse.setMsg("查询学生异常,"+e.getMessage());
        }
        return simpleResponse;
    }
}
