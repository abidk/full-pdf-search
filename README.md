# Full PDF Search

Just an example of how to index pdf files in Lucene.

## Development

Run the project: './gradlew bootRun'

Access the url: http://localhost:8080/search?query=the

## Notes

You can turn off that automatic conversion using git config core.autocrlf false.

Setting the line endings of gradlew back to Unix style fixed the problem. In Vim this is done using set fileformat=unix
