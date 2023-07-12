$('document').ready(function () {
    const apiUrl = 'http://localhost:8080';

    // create an Auth0 client
    const webAuth = new auth0.WebAuth({
        domain: "dev-7hxmzq06wywhbaqg.us.auth0.com",
        clientID: "4KBwlbVbIMpHuwjbWPedvx4iUfDbjtdf",
        redirectUri: location.href,
        audience: "https://footing.mindbridgehealth.com",
        responseType: 'token id_token',
        scope: 'openid profile email read:userdata',
        leeway: 60
    });

    // sections and buttons
    const homeView = $('#home-view');
    const profileView = $('#profile-view');
    const pingView = $('#ping-view');
    const onboardingView = $('#onboarding-view');
    const callPrivateMessage = $('#call-private-message');
    const pingMessage = $('#ping-message');
    const interviewView = $('#interview-view');

    // buttons
    const loginBtn = $('#btn-login');
    const logoutBtn = $('#btn-logout');
    const homeViewBtn = $('#btn-home-view');
    const profileViewBtn = $('#btn-profile-view');
    const pingViewBtn = $('#btn-ping-view');
    const onboardingViewBtn = $('#btn-onboarding-view');
    const pingPublic = $('#btn-ping-public');
    const pingPrivate = $('#btn-ping-private');
    const pingPrivateScoped = $('#btn-ping-private-scoped');
    const interviewBtn = $('#btn-interview-view');

    // listeners
    pingPublic.click(() => callAPI('/public', false));
    pingPrivate.click(() => callAPI('/private', true));
    pingPrivateScoped.click(() => callAPI('/storytellers/qihq1bu4i1h08d1cmi2m.113891357537506632609', true));
    loginBtn.click(() => webAuth.authorize());
    logoutBtn.click(logout);
    homeViewBtn.click(displayHome);
    profileViewBtn.click(displayProfile);
    pingViewBtn.click(displayPingView);
    onboardingViewBtn.click(displayOnboardingView);
    interviewBtn.click(displayInterviewView);


    let accessToken = null;
    let userProfile = null;

    handleAuthentication();
    displayButtons();

    // function definitions

    $('#myForm').submit((event) => {
        event.preventDefault()
        console.log('Calling Footing');
        const myForm = document.getElementById("myForm");

        const data = {
            firstname: myForm.firstname.value,
            lastname: myForm.lastname.value,
            mobile: myForm.mobile.value,
            contactMethod: myForm.contactMethod.value
        };

        console.log('data' + JSON.stringify(data));
        fetch('/storytellers/', {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${accessToken}`
            },
            body: JSON.stringify(data)
        }).then(response => response.json())
            .then(response => console.log(response))
    });

    $('#interview-schedule-form').submit((event) => {
        event.preventDefault();
        console.log("Scheduling Interview")

        let usePreferredTime = $('#usePreferredTime').prop('checked');
        let formattedDate = undefined
        if (!usePreferredTime) {
            const datetimeValue = document.getElementById('datePicker').value
            // Parse the datetime value using the Date object
            const parsedDate = new Date(datetimeValue);
            // Format the parsed date in the desired format
            formattedDate = parsedDate.toISOString().slice(0, 19) + 'Z';
        }
        // Get the value from the datetime-local input


        const qParams = {
            append: usePreferredTime,
            questionId: $('#question').find(":selected").val(),
            name: $('#interviewName').val()
        }
        if (!usePreferredTime && formattedDate !== undefined) {
            qParams.append("time", formattedDate)
        }
        const url = `/api/v1/interviews/scheduled/?` + new URLSearchParams(qParams)
        console.log("Calling " + url)
        fetch(url, {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${accessToken}`
            }
        }).then(response => {
            console.log(response)
            displayUpcomingInterviews();
        })
    })

    $('#preferred-time-form').submit((event) => {
        event.preventDefault();
        console.log("Adding preferred time")

        const datetimeValue = document.getElementById('preferredDatePicker').value
        // Parse the datetime value using the Date object
        const parsedDate = new Date(datetimeValue);
        // Format the parsed date in the desired format
        const time = formatTime(parsedDate);
        const dayOfWeek = getDayOfWeek(parsedDate);
        const pTime = {
            "time": time,
            "dayOfWeek": dayOfWeek
        }

        const url = `/api/v1/storytellers/`
        console.log("Calling " + url)
        $('preferredTimes').empty()
        fetch(url, {
            method: 'GET',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${accessToken}`
            }
        }).then(response => response.json())
            .then(storyteller => {
                if (!storyteller["preferredTimes"]) {
                    storyteller["preferredTimes"] = [pTime];
                } else {
                    storyteller["preferredTimes"].push(pTime)
                }
                fetch(url, {
                    method: 'PUT',
                    headers: {
                        'Accept': 'application/json',
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${accessToken}`
                    },
                    body: JSON.stringify(storyteller)
                }).then(response => console.log(response))
            })

        displayPreferredTimes();
    })

    function logout() {
        // Remove tokens and expiry time from browser
        accessToken = null;
        pingMessage.css('display', 'none');
        displayButtons();
    }

    function isAuthenticated() {
        return accessToken != null;
    }

    function handleAuthentication() {
        webAuth.parseHash(function (err, authResult) {
            if (authResult && authResult.accessToken) {
                window.location.hash = '';
                accessToken = authResult.accessToken;
                userProfile = authResult.idTokenPayload;
                loginBtn.css('display', 'none');
                homeView.css('display', 'inline-block');
            } else if (err) {
                homeView.css('display', 'inline-block');
                console.log(err);
                alert(
                    'Error: ' + err.error + '. Check the console for further details.'
                );
            }
            displayButtons();
        });
    }

    function callAPI(endpoint, secured) {
        const url = apiUrl + endpoint;

        let headers;
        if (secured && accessToken) {
            headers = {Authorization: 'Bearer ' + accessToken};
        }

        $.ajax({
            url: url,
            headers: headers
        }).done(({message}) => $('#ping-view h2').text(message))
            .fail(({statusText}) => $('#ping-view h2').text('Request failed: ' + statusText));
    }

    function displayButtons() {
        const loginStatus = $('.container h4');
        if (isAuthenticated()) {
            loginBtn.css('display', 'none');
            logoutBtn.css('display', 'inline-block');
            profileViewBtn.css('display', 'inline-block');
            pingPrivate.css('display', 'inline-block');
            pingPrivateScoped.css('display', 'inline-block');
            callPrivateMessage.css('display', 'none');
            loginStatus.text(
                'You are logged in! You can now send authenticated requests to your server.'
            );
        } else {
            homeView.css('display', 'inline-block');
            loginBtn.css('display', 'inline-block');
            logoutBtn.css('display', 'none');
            profileViewBtn.css('display', 'none');
            profileView.css('display', 'none');
            pingView.css('display', 'none');
            pingPrivate.css('display', 'none');
            pingPrivateScoped.css('display', 'none');
            callPrivateMessage.css('display', 'block');
            loginStatus.text('You are not logged in! Please log in to continue.');
        }
    }

    function displayHome() {
        homeView.css('display', 'inline-block');
        profileView.css('display', 'none');
        pingView.css('display', 'none');
        interviewView.css('display', 'none');
        onboardingView.css('display', 'none');
    }

    function displayProfile() {
        // display the elements
        homeView.css('display', 'none');
        pingView.css('display', 'none');
        interviewView.css('display', 'none');
        profileView.css('display', 'inline-block');
        onboardingView.css('display', 'none');

        // display profile data
        $('#profile-view .nickname').text(userProfile.nickname);
        $('#profile-view .full-profile').text(JSON.stringify(userProfile, null, 2));
        $('#profile-view img').attr('src', userProfile.picture);
        $('#profile-view .jwt').text(accessToken);
    }

    function displayPingView() {
        homeView.css('display', 'none');
        profileView.css('display', 'none');
        interviewView.css('display', 'none');
        pingView.css('display', 'inline-block');
        onboardingView.css('display', 'none');
    }

    function displayOnboardingView() {
        homeView.css('display', 'none');
        profileView.css('display', 'none');
        pingView.css('display', 'none');
        onboardingView.css('display', 'inline-block');
        interviewView.css('display', 'none');
    }


    function getQuestions() {
        const headers = {
            Authorization: 'Bearer ' + accessToken
        };

        console.log("getting questions")

        fetch("/api/v1/questions/", {
            method: 'GET',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${accessToken}`
            },
        }).then(response => response.json())
            .then(questions => {
                console.log(questions)
                $.each(questions, function (index, obj) {
                    console.log(obj.id + ' ' + obj.text)
                    const option = $('<option></option>').attr('value', obj.id).text(obj.text);
                    $('#question').append(option);
                })
            })
    }

    function displayInterviewView() {
        homeView.css('display', 'none');
        profileView.css('display', 'none');
        pingView.css('display', 'none');
        onboardingView.css('display', 'none');
        interviewView.css('display', 'inline-block');

        displayUpcomingInterviews();
        displayPreferredTimes();
        getQuestions();
    }

    function displayUpcomingInterviews() {

        const url = `/api/v1/interviews/storytellers/self/scheduled:all`
        console.log("Calling " + url)
        $('#upcomingInterviews').empty()
        fetch(url, {
            method: 'GET',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${accessToken}`
            }
        }).then(response => response.json())
            .then(interviews => {
                console.log(JSON.stringify(interviews))
                $.each(interviews, function (index, obj) {
                    const row = $('<tr></tr>');
                    const nameCell = $('<td></td>').text(obj.name);
                    const scheduledTimeCell = $('<td></td>').text(obj.scheduledTime);
                    row.append(nameCell, scheduledTimeCell);
                    $('#upcomingInterviews').append(row);
                });
            })
    }

    function displayPreferredTimes() {
        const url = `/api/v1/storytellers/`
        console.log("Calling " + url)
        $('preferredTimes').empty()
        fetch(url, {
            method: 'GET',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${accessToken}`
            }
        }).then(response => response.json())
            .then(storyteller => {
                let preferredTimes = storyteller["preferredTimes"];
                $.each(preferredTimes, function (index, obj) {
                    console.log(obj)
                    console.log(obj.time)
                    console.log(obj.dayOfWeek)
                    const row = $('<tr></tr>');
                    const dayOfWeek = $('<td></td>').text(obj.dayOfWeek);
                    const time = $('<td></td>').text(obj.time);
                    row.append(dayOfWeek, time);
                    $('#preferredTimes').append(row)
                })
            })
    }

// Helper function to format the time as "HH:mm:ss"
    function formatTime(date) {
        var hours = String(date.getHours()).padStart(2, '0');
        var minutes = String(date.getMinutes()).padStart(2, '0');
        var seconds = String(date.getSeconds()).padStart(2, '0');
        return hours + ':' + minutes + ':' + seconds;
    }

// Helper function to get the day of the week as a string
    function getDayOfWeek(date) {
        var options = {weekday: 'long'};
        return date.toLocaleDateString(undefined, options).toUpperCase();
    }

});