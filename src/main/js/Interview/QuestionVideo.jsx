import React, {useEffect, useState} from 'react';

function QuestionVideo({interviewQuestionId}) {


    return (
        <>
            <video
                id="my-video"
                class="video-js"
                style="width:100%;height:100%;object-fit: cover;"
                controls
                preload="auto"
                data-setup='{"responsive": true, "inactivityTimeout": 0}'
            >
                <source src="https://ec-tomcat.s3.amazonaws.com/{{interviewQuestionId}}.mp4#t=0.1" type="video/mp4" />
                <p class="vjs-no-js">
                    To view this video please enable JavaScript, and consider upgrading to a
                    web browser that
                    <a href="https://videojs.com/html5-video-support/" target="_blank"
                    >supports HTML5 video</a
                    >
                </p>
            </video>
        </>
    )
}

export default QuestionVideo
