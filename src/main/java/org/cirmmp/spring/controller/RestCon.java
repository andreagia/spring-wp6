package org.cirmmp.spring.controller;


import com.google.gson.Gson;
import org.cirmmp.spring.exception.EmployeeNotFoundException;
import org.cirmmp.spring.model.FileList;
import org.cirmmp.spring.model.Project;
import org.cirmmp.spring.model.User;
import org.cirmmp.spring.model.json.JFileList;
import org.cirmmp.spring.model.json.JProject;
import org.cirmmp.spring.service.FileListService;
import org.cirmmp.spring.service.ProjectService;
import org.cirmmp.spring.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/restcon")
public class RestCon {
    private static final Logger LOG = LoggerFactory.getLogger(RestCon.class);

    @Autowired
    UserService userService;

    @Autowired
    ProjectService projectService;

    @Autowired
    FileListService fileListService;

    //@Autowired
    //LinkService linkService;

    //@Autowired
    //OfferService offerService;

    //@Autowired
    //OrdiniService ordiniService;



    @RequestMapping(value="/filelistE/{id}", method=RequestMethod.GET)
    public ResponseEntity getEmployee(@PathVariable("id") String i) throws Exception{
        //deliberately throwing different types of exception
        LOG.info("sono in createproject");
        int id = Integer.parseInt(i);
        String ssoId = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findBySSO(ssoId);
        //List<Project> projects =projectService.findByUserId(user.getId());
        if (id<1){
            throw new EmployeeNotFoundException(1);
        } else if(id == 2){
            throw new SQLException("SQLException, id="+id);
        }
        else {
            return new ResponseEntity("pippo", HttpStatus.OK);
        }
//
//        if(id==1){
//            throw new EmployeeNotFoundException(id);
//        }else if(id==2){
//            throw new SQLException("SQLException, id="+id);
//        }else if(id==3){
//            throw new IOException("IOException, id="+id);
//        }else if(id==10){
//            Employee emp = new Employee();
//            emp.setName("Pankaj");
//            emp.setId(id);
//            model.addAttribute("employee", emp);
//            return new ResponseEntity(projects, HttpStatus.OK);
//        }else {
//            throw new Exception("Generic Exception, id="+id);
//        }

    }

    @ExceptionHandler(NumberFormatException.class)
    public ModelAndView exceptionHandlerNumber (HttpServletRequest request, Exception ex) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("exception", ex);
        modelAndView.addObject("url", request.getRequestURL());

        modelAndView.setViewName("error");
        return modelAndView;
    }

    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    @ExceptionHandler(CannotCreateTransactionException.class)
    public ModelAndView exceptionHandlerHibernate (HttpServletRequest request, Exception ex) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("exception", ex);
        modelAndView.addObject("url", request.getRequestURL());

        modelAndView.setViewName("error");
        return modelAndView;
    }
    @ExceptionHandler(EmployeeNotFoundException.class)
    public ModelAndView handleEmployeeNotFoundException(HttpServletRequest request, Exception ex){
        LOG.error("Requested URL="+request.getRequestURL());
        LOG.error("Exception Raised="+ex);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("exception", ex);
        modelAndView.addObject("url", request.getRequestURL());

        modelAndView.setViewName("error");
        return modelAndView;
    }


    @RequestMapping(value = { "/user" },method = RequestMethod.GET,produces = {"application/json"})
    public ResponseEntity listOrder() {
        LOG.info("sono in user");
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        //return "Welcome, " + username;
        Gson gson = new Gson();
        //List<Offer> offers = offerService.findAllOffer();
        //String jsonOffers = gson.toJson(username);
        LOG.info("JSON OFFERS");
        LOG.info(username);
        return new ResponseEntity(username, HttpStatus.OK);
    }

    @RequestMapping(value = "/createProjectTest")
    public ResponseEntity createProjectTest(){
        LOG.info("sono in createproject");

        String ssoId = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findBySSO(ssoId);
        Date now = new Date();
        Project project = new Project();
        project.setCreation_date(now);
        project.setUserId(user.getId());
        project.setProjectName("Test");
        project.setSummary("TEST TEST TEST");
        LOG.info("sono in createproject 2");
        projectService.save(project);
        return new ResponseEntity(project, HttpStatus.OK);

    }

    @RequestMapping(value = "/createProject", method = RequestMethod.POST,consumes = {"application/json"},produces = {"application/json"})
    public ResponseEntity createProject(@RequestBody JProject jProject){
        String ssoId = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findBySSO(ssoId);
        Date now = new Date();
        Project project = new Project();
        project.setCreation_date(now);
        project.setUserId(user.getId());
        project.setProjectName(jProject.getProjectName());
        project.setSummary(jProject.getSummary());
        projectService.save(project);
        return new ResponseEntity(project, HttpStatus.OK);
    }

    @RequestMapping(value = "/listProject")
    public ResponseEntity listProject(){
        LOG.info("sono in createproject");
        String ssoId = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findBySSO(ssoId);
        List<Project> projects =projectService.findByUserId(user.getId());
        return new ResponseEntity(projects, HttpStatus.OK);
    }

//    @RequestMapping(value = { "/filelist-{projectId}" }, method = RequestMethod.GET)
//    public ResponseEntity listFilesId(@PathVariable Long projectId) {
//
//        Project project = projectService.findById(projectId);
//        List<FileList> files = project.getFileLists();
//        ArrayList<JFileList> nfiles = new ArrayList<>();
//        for(FileList ifile :files){
//            JFileList infiles = new JFileList();
//            infiles.setFiletName(ifile.getFileName());
//            infiles.setFileInfo(ifile.getFileInfo());
//           // infiles.setProjectId(ifile.getProjectId());
//            nfiles.add(infiles);
//        }
//
//        return new ResponseEntity(nfiles, HttpStatus.OK);
//    }

    @RequestMapping(value = { "/filelist" }, method = RequestMethod.GET)
    public ResponseEntity listFiles() {

        List<FileList> files = fileListService.findAllFiles();
        ArrayList<JFileList> nfiles = new ArrayList<>();
        for(FileList ifile :files){
            JFileList infiles = new JFileList();
            infiles.setFiletName(ifile.getFileName());
            infiles.setFileInfo(ifile.getFileInfo());
            //infiles.setProjectId(ifile.getProjectId());
            infiles.setCreation_date(ifile.getCreation_date());
            infiles.setSummary(infiles.getSummary());
            infiles.setWebdavurl(infiles.getWebdavurl());
            nfiles.add(infiles);
        }
        return new ResponseEntity(nfiles, HttpStatus.OK);
    }
}
