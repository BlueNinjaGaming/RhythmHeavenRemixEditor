# News API

>Note: This likely only concerns you if you are a developer looking to
implement the News API for yourself.

## Rate Limits
| Header | Desc. | Example Value |
|--------|-------|---------------|
| X-RateLimit-Limit | The maximum amount of actions in a given interval. | 15 |
| X-RateLimit-Remaining | The number of actions remaining for the current interval. | 13 |
| X-RateLimit-Reset | The Epoch milliseconds for when the rate limit resets. | 1522627193 |
| X-RateLimit-Window | The rate limit interval in minutes. | 15 |

For example, if given:
```
X-RateLimit-Limit     → 15
X-RateLimit-Remaining → 13
X-RateLimit-Reset     → 1522627193000
X-RateLimit-Window    → 30
```
as part of the response headers, one can determine that there are 13 of 15 actions
remaining for this 30 minute interval, which resets at Epoch milliseconds 1522627193000.

## Paths
### **GET** `/articles`

Returns a json array of recent Articles with any count greater than zero, or 404 if non-existent.

### **GET** `/articles/{article.id}`
Returns the Article with the given ID, or 404 if non-existent.

## JSON Objects
### Article
Represents a news article.

| Field | Type | Description |
|-------|------|-------------|
| id | string | The article's unique ID. |
| title | string | The article's title. |
| body | string | The article's body. |
| thumbnail | string | The article's thumbnail URL. May be blank, but never null. |
| publishedAt | long | The Epoch milliseconds of publication. |
| url | string? | If not null, provides a URL to go to with a button. |
| images | string[] | A string URL array of image URLs. May be empty, but is never null. |


##### Example
```json
{
  "id": "welcomeToRHRE",
  "title": "Welcome to RHRE!",
  "body": "This is an example body.\nIt has multiple lines in it.",
  "thumbnail": "http://i.imgur.com/X4Vs7D0.png",
  "publishedAt": 1522627193000,
  "url": "https://github.com/chrislo27/RhythmHeavenRemixEditor",
  "images": ["https://i.imgur.com/ERHnJ2O.png"]
}
```