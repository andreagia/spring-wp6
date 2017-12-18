package org.cirmmp.spring.controller;


import com.google.gson.Gson;
import org.cirmmp.spring.model.FileList;
import org.cirmmp.spring.model.Project;
import org.cirmmp.spring.model.UploadModel;
import org.cirmmp.spring.model.User;
import org.cirmmp.spring.model.json.JFileList;
import org.cirmmp.spring.model.json.JProject;
import org.cirmmp.spring.service.FileListService;
import org.cirmmp.spring.service.ProjectService;
import org.cirmmp.spring.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
}
