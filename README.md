# Recruiting Process

Rest API for dealing with recruitment process (for studying purpose).

## Usage

`http://localhost:8080/recruitment-process`

### Create a job offer:

**POST** `/api/offers/`

#### Request body:
```json
{
    "jobTitle": "{offer title}}",
    "startDate": "{start date}}" (optional)
}
```

### Read a single offer (track the number of applications):

**GET** `/api/offers/{offerTitle}`

### List all offers (track the number of applications):

**GET** `/api/offers/`

### Apply for an offer:

**POST** `/api/applications/`

#### Request body:
```json
{
    "jobOffer": "{offer title}",
    "candidateEmail": "{application email}",
    "resumeText": "{resume}" (optional)
}
```

### Read one application of an offer:

**GET** `/api/applications/{offerTitle}/{applicationEmail}`

### List all applications of an offer:

**GET** `/api/applications/{offerTitle}`

### Progress the status of an application:

**PUT** `/api/applications/`

#### Request body:

```json
{
    "jobOffer": "{offer title}",
    "candidateEmail": "{email}}",
    "status": "{status}"
}
```