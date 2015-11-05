Updating PDF Validator documentation
====================================

Our documentation is written using [MkDocs](http://www.mkdocs.org/) static documentation site generator and 
language that we use to write documentation is [Markdown](https://daringfireball.net/projects/markdown/). 

System requirements
-------------------

* **Python 2** - All version above 2.6 should work
* **pip** - Python package manager
* **MkDocs CLI** - to generate and deploy new version of documentation
* **Text Editor** - to edit Markdown documents (i.e [Haroopad](http://pad.haroopress.com/#))

Installing MkDocs
-----------------

### Ubuntu and Mac OS X

Both Ubuntu and Mac OS X come `python` version 2 already installed You only need to install `pip`

1.  Install `pip` on Ubuntu 15.04 `sudo apt-get install python-pip` on Mac OS X `sudo easy_install pip`
2.  Next up install `mkdocs` using [pip](https://pip.pypa.io/en/stable/): `pip install mkdocs`

Now You done and can start editing the PDF Validator documentation. 

### Windows

Editing content
---------------

1.  Edit markdown files inside the `docs` directory
2.  Preview Your changes by issuing `mkdocs serve` and navigating to <http://localhost:8000>
3.  Commit and push Your changes to `git`

> **NOTE** If You want to add image files please place them inside `$PROJECT_ROOT/docs/img` directory

> **NOTE** If You need to override some of the CSS properties place these changes 
> inside the `$PROJECT_ROOT/docs/override.css` file

Deploying changes to GitHub pages
---------------------------------

1.  Generate the static site `mkdocs build`
2.  Deploy the generated site to GitHub Pages: `mkdocs gh-deploy` 
3.  You are done and can go check the updated documentation at: <http://open-eid.github.io/pdf-validator>
