const https = require('https');
const fs = require('fs');

// Function to fetch coordinates from the API
function fetchCoordinates(address, retryCount = 0) {
    return new Promise((resolve, reject) => {
        const apiKey = '675aaff60c2c2751422028qef8dca00';
        const url = `https://geocode.maps.co/search?q=${encodeURIComponent(address)}&api_key=${apiKey}`;

        https.get(url, (res) => {
            let data = '';

            res.on('data', (chunk) => {
                data += chunk;
            });

            res.on('end', () => {
                if (res.statusCode === 200) {
                    try {
                        const jsonData = JSON.parse(data);
                        if (jsonData.length > 0) {
                            resolve({
                                lat: jsonData[0].lat,
                                lon: jsonData[0].lon
                            });
                        } else {
                            reject(new Error(`No coordinates found for address: ${address}`));
                        }
                    } catch (error) {
                        reject(new Error(`Error parsing JSON response for address: ${address}`));
                    }
                } else if (res.statusCode === 429) {
                    // Handle rate limit error with exponential backoff
                    if (retryCount < 5) { // Maximum 5 retries
                        const delay = Math.pow(2, retryCount) * 1000;
                        console.warn(`Rate limit exceeded. Retrying in ${delay / 1000} seconds...`);
                        setTimeout(() => {
                            fetchCoordinates(address, retryCount + 1).then(resolve).catch(reject);
                        }, delay);
                    } else {
                        reject(new Error(`Rate limit exceeded for address: ${address}. Maximum retries reached.`));
                    }
                } else {
                    reject(new Error(`HTTP ${res.statusCode}: ${data}`));
                }
            });
        }).on('error', (err) => {
            reject(err);
        });
    });
}

// Function to update coordinates for a given file
async function updateCoordinatesForFile(inputFile, outputFile) {
    const data = JSON.parse(fs.readFileSync(inputFile, 'utf8'));

    for (const city in data) {
        for (const item of data[city]) {
            try {
                console.log(`Fetching coordinates for ${item.address}...`);
                const coordinates = await fetchCoordinates(item.address);
                item.lat = coordinates.lat;
                item.lon = coordinates.lon;
                console.log(`Coordinates fetched for ${item.address}: ${coordinates.lat}, ${coordinates.lon}`);
            } catch (error) {
                console.error(`Error fetching coordinates for ${item.address}: ${error.message}`);
            }
            await new Promise(resolve => setTimeout(resolve, 1000));
        }
    }

    fs.writeFileSync(outputFile, JSON.stringify(data, null, 2));
    console.log(`\n\nUpdated data written to ${outputFile}.\n\n`);
}

// Run the update function for both files
async function updateAllFiles() {
    await updateCoordinatesForFile('activites.json', 'updated_activites.json');
    await updateCoordinatesForFile('hotels.json', 'updated_hotels.json');
}

updateAllFiles();