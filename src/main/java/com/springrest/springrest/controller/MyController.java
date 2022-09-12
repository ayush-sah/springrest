package com.springrest.springrest.controller;

import com.springrest.springrest.encryption.AESUtil;
import com.springrest.springrest.entities.Course;
import com.springrest.springrest.services.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@RestController
public class MyController {

    @Autowired
    public CourseService courseService;

    SecretKey key = AESUtil.generateKey(128);
    IvParameterSpec ivParameterSpec = AESUtil.generateIv();
    String algorithm = "AES/CBC/PKCS5Padding";

    public MyController() throws NoSuchAlgorithmException {
    }

    //get the courses
    @GetMapping("/courses")
    public List<Course> getCourses() throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        List<Course> courses = this.courseService.getCourses();
        for (Course course: courses) {
            course.setTitle(AESUtil.decrypt(algorithm, course.getTitle(), key, ivParameterSpec));
            course.setDescription(AESUtil.decrypt(algorithm, course.getDescription(), key, ivParameterSpec));
        }
        return courses;
    }

    @GetMapping("/courses/{courseId}")
    public Course getCourse(@PathVariable String courseId) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        Course course =  this.courseService.getCourse(Long.parseLong(courseId));
        course.setTitle(AESUtil.decrypt(algorithm, course.getTitle(), key, ivParameterSpec));
        course.setDescription(AESUtil.decrypt(algorithm, course.getDescription(), key, ivParameterSpec));
        return course;
    }
    @PostMapping("/courses")
    public Course addCourse(@RequestBody Course course) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        course.setTitle(AESUtil.encrypt(algorithm, course.getTitle(), key, ivParameterSpec));
        course.setDescription(AESUtil.encrypt(algorithm, course.getDescription(), key, ivParameterSpec));
        return this.courseService.addCourse(course);
    }

    @PutMapping("/courses")
    public Course updateCourse(@RequestBody Course course) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        course.setTitle(AESUtil.encrypt(algorithm, course.getTitle(), key, ivParameterSpec));
        course.setDescription(AESUtil.encrypt(algorithm, course.getDescription(), key, ivParameterSpec));
        return this.courseService.updateCourse(course);
    }

    @DeleteMapping("/courses/{courseId}")
    public ResponseEntity<HttpStatus> deleteCourse(@PathVariable String courseId){
        try{
            this.courseService.deleteCourse(Long.parseLong(courseId));
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
