package com.palaver.data

import com.palaver.data.entity.MediaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface MediaRepository: JpaRepository<MediaEntity, Int>