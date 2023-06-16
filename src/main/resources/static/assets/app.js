$('document').ready(function() {
    const apiUrl = 'http://localhost:8080';

    // load environment variables
    const envVar = $.parseJSON($.ajax({
        url:  '/config',
        dataType: 'json',
        async: false
    }).responseText);

    // create an Auth0 client
    const webAuth = new auth0.WebAuth({
        domain: "dev-7hxmzq06wywhbaqg.us.auth0.com",
        clientID: "4KBwlbVbIMpHuwjbWPedvx4iUfDbjtdf",
        redirectUri: "http://localhost:8080/",
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

    let accessToken = null;
    let userProfile = null;

    document.getElementById('myForm').addEventListener('submit', submitForm);

    handleAuthentication();
    displayButtons();

    // function definitions

    function submitForm(event){
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
    }

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
        webAuth.parseHash(function(err, authResult) {
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
            headers = { Authorization: 'Bearer ' + accessToken };
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
    }

    function displayProfile() {
        // display the elements
        homeView.css('display', 'none');
        pingView.css('display', 'none');
        profileView.css('display', 'inline-block');

        // display profile data
        $('#profile-view .nickname').text(userProfile.nickname);
        $('#profile-view .full-profile').text(JSON.stringify(userProfile, null, 2));
        $('#profile-view img').attr('src', userProfile.picture);
        $('#profile-view .jwt').text(accessToken);
    }

    function displayPingView() {
        homeView.css('display', 'none');
        profileView.css('display', 'none');
        pingView.css('display', 'inline-block');
    }

    function displayOnboardingView() {
        homeView.css('display', 'none');
        profileView.css('display', 'none');
        pingView.css('display', 'none');
        onboardingView.css('display', 'inline-block');
    }
});