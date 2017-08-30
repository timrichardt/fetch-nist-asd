# fetch-nist-asd

This is a collection of Clojure functions to access the line data
of the [Atomic Spectra Database Version 5](https://www.nist.gov/pml/atomic-spectra-database) of the [NIST](https://www.nist.gov).

An index of all accessible data is available under

```
http://physics.nist.gov/cgi-bin/ASD/lines_pt.pl
```

and the line data itself are accessed through

```
http://physics.nist.gov/cgi-bin/ASD/lines1.pl
```

via a `GET` request.

The query parameters are documented in the [fetch](src/fetch_nist_asd/fetch.clj)
namespace.

The line data is returned in form of a ASCII table. Functions
to parse some of the data from the table can be found in the
namespace [parse](src/fetch_nist_asd/fetch.clj).

## License

[MIT](https://opensource.org/licenses/MIT)
