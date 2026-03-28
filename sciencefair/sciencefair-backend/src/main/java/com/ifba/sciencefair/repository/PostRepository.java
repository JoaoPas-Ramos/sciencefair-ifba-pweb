package com.ifba.sciencefair.repository;

import com.ifba.sciencefair.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}