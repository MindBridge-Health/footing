package com.mindbridgehealth.footing.api.controller

import com.mindbridgehealth.footing.api.dto.addpipe.*
import com.mindbridgehealth.footing.service.MediaService
import io.mockk.mockk
import org.junit.jupiter.api.Test
import java.net.URI
import kotlin.test.assertEquals

class MediaControllerTests {

//    @Test
//    fun addPipeCallback_VideoRecordedEvent_MappedCorrectly() {
//
//        val mediaService = mockk<MediaService>()
//        val mediaController = MediaController(mediaService)
//
//        val returnVal = mediaController.addPipeCallback( VideoRecordedEvent("v1", VideoRecordedEventData("","","","",1,"","","","","","","", "")))
//        assertEquals("videoRecorded", returnVal)
//    }
//
//    @Test
//    fun addPipeCallback_VideoConvertedEvent_MappedCorrectly() {
//
//        val mediaService = mockk<MediaService>()
//        val mediaController = MediaController(mediaService)
//
//        val returnVal = mediaController.addPipeCallback( VideoConvertedEvent("v1", VideoConvertedEventData("",1,"","","",1,"","","",2,"","","")))
//        assertEquals("videoConverted", returnVal)
//    }
//
//    @Test
//    fun addPipeCallback_VideoCopiedPipeS3Event_MappedCorrectly() {
//
//        val mediaService = mockk<MediaService>()
//        val mediaController = MediaController(mediaService)
//
//        val returnVal = mediaController.addPipeCallback( VideoCopiedPipeS3Event("v1", VideoCopiedPipeS3EventData("","",1,"","",1,
//            URI.create("http://localhost").toURL(),URI.create("http://localhost").toURL(),URI.create("http://localhost").toURL(),URI.create("http://localhost").toURL(),
//            CdnData(URI.create("http://localhost").toURL(), URI.create("http://localhost").toURL(), URI.create("http://localhost").toURL(), URI.create("http://localhost").toURL()),"", "", "")))
//        assertEquals("videoCopied", returnVal)
//    }
//
//    @Test
//    fun addPipeCallback_UnknownEvent_MappedCorrectly() {
//
//        val mediaService = mockk<MediaService>()
//        val mediaController = MediaController(mediaService)
//
//        class unknownEvent(event: String): AddPipeEvent(event)
//
//        val returnVal = mediaController.addPipeCallback( unknownEvent("unknown"))
//        assertEquals("unknown", returnVal)
//    }
}