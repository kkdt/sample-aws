# Sample AWS

## Prerequisites

1. Cognito User Pool (this is the only required identity provider)
2. AWS Region
3. AWS Client App ID configured for User Pool
4. Identity Pool created and associated to User Pool (and optional providers)
5. Identity Provider ID

Additional identity providers may be configured and their client ID/secret properties must be configured.

## Quick Start

1. Create a file `${HOME}/sampleaws-cognito.properties` that has the following properties
  ```
  cognito.title - Window title
  cognito.poolid - User Pool
  cognito.region - AWS Region
  cognito.clientid - AWS Client App ID configured for User Pool
  cognito.identitypool - AWS Cognito Identity Pool
  cognito.identityprovider - Default provider identity pool
  google.clientId - Google API client ID
  google.clientSecret - Google API client secret
  ```
2. `gradle clean build` - This will create a `zip` distribution file in `rootProject/build`
3. Unzip the distribution file
4. Execute
  ```
  `<unzipped>/bin/sampleaws-cognito` (Unix/Linux)
  `<unzipped>/bin/sampleaws-cognito.bat` (Windows)
  ```

