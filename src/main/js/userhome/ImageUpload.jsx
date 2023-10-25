import React, {useEffect, useState} from 'react';
import {useAuth0} from "@auth0/auth0-react";

function ImageUpload({storytellerId, newImageCallback}) {
    const {  getAccessTokenSilently } = useAuth0();
    const [selectedFiles, setSelectedFiles] = useState([]);
    const [uploadProgress, setUploadProgress] = useState({});
    const [accessToken, setAccessToken] = useState("")

    useEffect(() => {
        (async () => {setAccessToken(await getAccessTokenSilently())})();
    }, [getAccessTokenSilently]);

    const handleFileChange = (event) => {
        setSelectedFiles([...event.target.files]);
    };

    const handleUpload = async () => {
        try {
            if (selectedFiles.length === 0) {
                console.error('No files selected.');
                return;
            }

            const progressBars = {};
            selectedFiles.forEach((selectedFile) => {
                progressBars[selectedFile.name] = 0;
            });
            setUploadProgress(progressBars);

            for (const selectedFile of selectedFiles) {
                const imageName = `${storytellerId}_${selectedFile.name}`
                const endpointUrl = storytellerId
                    ? `/api/v1/uploads/images/storytellers/${imageName}/upload-url`
                    : `/api/v1/uploads/images/upload-url`;
                const response = await fetch(endpointUrl, {
                    headers: {
                        Authorization: `Bearer ${accessToken}`,
                    },
                });

                if (!response.ok) {
                    console.error('Error getting upload URL:', response.statusText);
                    return;
                }

                const url  = await response.text();

                const options = {
                    method: 'PUT',
                    headers: {
                        'Content-Type': selectedFile.type,
                    },
                    body: selectedFile,
                };

                const uploadRequest = await fetch(url, options);

                if(uploadRequest.ok) {
                    setUploadProgress((prevProgress) => ({
                        ...prevProgress,
                        [selectedFile.name]: 100,
                    }));
                    newImageCallback(imageName)
                } else {

                }
            }

            // Handle success or other actions after all uploads
        } catch (error) {
            console.error('Error uploading image:', error);
        }
    };

    return (
        <div>
            <input type="file" multiple onChange={handleFileChange} />
            <button onClick={handleUpload}>Upload Images</button>
            {selectedFiles.map((selectedFile) => (
                <div key={selectedFile.name}>
                    <p>{selectedFile.name}</p>
                    <progress value={uploadProgress[selectedFile.name] || 0} max={100} />
                </div>
            ))}
        </div>
    );
}

export default ImageUpload;
