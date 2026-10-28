import groovy.io.FileType
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import java.security.MessageDigest
import java.util.regex.Pattern
import java.util.zip.ZipFile
import java.util.zip.ZipInputStream

def setting = { String key, fallback = null ->
	session.userProperties.getProperty(key) ?: (binding.hasVariable(key) ? binding.getVariable(key) : null) ?: project.properties.getProperty(key) ?: fallback
}

def potix = [name: 'Potix Corporation', url: ['https://www.zkoss.org']]
def zkLicence = 'LGPL-2.1-only'
def zkModuleLicences = [zel: 'Apache-2.0'] // a fork of Apache EL

def sbomCreators = [
	'org.zkoss'                  : potix,
	'args4j'                     : [name: 'Kohsuke Kawaguchi', url: ['https://github.com/kohsuke/args4j']],
	'com.google'                 : [name: 'Google LLC', url: ['https://opensource.google']],
	'commons-fileupload'         : [name: 'The Apache Software Foundation', url: ['https://www.apache.org']],
	'commons-io'                 : [name: 'The Apache Software Foundation', url: ['https://www.apache.org']],
	'javax'                      : [name: 'Oracle Corporation', url: ['https://javaee.github.io']],
	'javax.enterprise'           : [name: 'Red Hat, Inc.', url: ['https://www.cdi-spec.org']],
	'javax.inject'               : [name: 'JSR-330 Expert Group', url: ['https://github.com/javax-inject/javax-inject']],
	'org.apache'                 : [name: 'The Apache Software Foundation', url: ['https://www.apache.org']],
	'org.acegisecurity'          : [name: 'Acegi Security', url: ['https://github.com/spring-projects/spring-security']],
	'org.apache-extras.beanshell': [name: 'BeanShell', url: ['https://github.com/beanshell/beanshell']],
	'org.codehaus.groovy'        : [name: 'The Apache Software Foundation', url: ['https://groovy-lang.org']],
	'org.hibernate'              : [name: 'Red Hat, Inc.', url: ['https://hibernate.org']],
	'org.javassist'              : [name: 'Shigeru Chiba', url: ['https://www.javassist.org']],
	'org.jruby'                  : [name: 'JRuby', url: ['https://www.jruby.org']],
	'org.jspecify'               : [name: 'JSpecify', url: ['https://jspecify.dev']],
	'org.mozilla'                : [name: 'The Mozilla Foundation', url: ['https://github.com/mozilla/rhino']],
	'org.owasp.encoder'          : [name: 'OWASP Foundation', url: ['https://owasp.org/www-project-java-encoder/']],
	'org.python'                 : [name: 'Jython Project', url: ['https://www.jython.org']],
	'org.seasar'                 : [name: 'The Seasar Foundation', url: ['https://www.seasar.org']],
	'org.slf4j'                  : [name: 'QOS.ch', url: ['https://www.slf4j.org']],
	'org.springframework'        : [name: 'Spring IO', url: ['https://spring.io']],
]

def sbomLicences = [
	'com.google.re2j:re2j'                   : 'BSD-3-Clause',
	'javax.el:javax.el-api'                  : 'CDDL-1.1 OR GPL-2.0-only WITH Classpath-exception-2.0',
	'javax.interceptor:javax.interceptor-api': 'CDDL-1.1 OR GPL-2.0-only WITH Classpath-exception-2.0',
	'javax.persistence:persistence-api'      : 'CDDL-1.0',
	'javax.transaction:jta'                  : 'CDDL-1.0',
	'org.jruby:jruby-core'                   : 'EPL-1.0 OR GPL-2.0-or-later OR LGPL-2.1-or-later',
	'org.python:jython'                      : 'LicenseRef-scancode-jython',
	'org.seasar.container:s2-framework'      : 'Apache-2.0',
	'org.zkoss.zkforge.el:zcommons-el'       : 'Apache-1.1',
]

def sbomVendored = [
	zcommon: [
		// outside web/, so the scan below misses them
		[file: 'metainfo/xml/XMLSchema.dtd', also: ['metainfo/xml/datatypes.dtd', 'metainfo/xml/xml.xsd'], name: 'xml-schema-dtd', purl: 'pkg:generic/apache/xml-schema-dtd', version: '1.0',
		 sha256: '5ab6a0e0a2fe3ba27643ab9c8953a90b53a079a5e3702d1e90e4563534654087', licence: 'Apache-2.0',
		 creator: [name: 'The Apache Software Foundation', url: ['https://www.apache.org']], executable: false],
		[file: 'metainfo/xml/xhtml1-strict.dtd', also: ['metainfo/xml/xhtml1-transitional.dtd', 'metainfo/xml/xhtml1-frameset.dtd', 'metainfo/xml/xhtml-lat1.ent', 'metainfo/xml/xhtml-special.ent', 'metainfo/xml/xhtml-symbol.ent'],
		 name: 'xhtml1-dtd', purl: 'pkg:generic/w3c/xhtml1-dtd', version: '1.0', sha256: '56cd2441229d82e30fb936b98adae91dd5a72e96fa78e822aa7d7bc4f21da13c', licence: 'W3C',
		 creator: [name: 'World Wide Web Consortium', url: ['https://www.w3.org']], executable: false],
		// copied into ZK's Java sources (src/, beside src/archive) and compiled into many classes, so multi; a stamp checks the copy is still there
		[file: '../org/zkoss/lang/JavaScriptEscape.java', name: 'unbescape', purl: 'pkg:maven/org.unbescape/unbescape',
		 version: '2015-09-03T16:40:33+08:00', upstream: null, stamp: /The UNBESCAPE team/, licence: 'Apache-2.0',
		 creator: [name: 'The UNBESCAPE team', url: ['https://www.unbescape.org']], patched: true, multi: true],
		[file: '../org/zkoss/idom/Verifier.java', name: 'jdom2', purl: 'pkg:maven/org.jdom/jdom2',
		 version: '2014-06-11T09:45:05+01:00', upstream: null, stamp: /jdom2/, licence: 'LicenseRef-JDOM',
		 creator: [name: 'Jason Hunter & Brett McLaughlin', url: ['http://www.jdom.org']], patched: true, multi: true],
		[file: '../org/zkoss/json/JSONObject.java', name: 'json-simple', purl: 'pkg:maven/com.googlecode.json-simple/json-simple',
		 version: '2022-05-24T14:32:59+08:00', upstream: null, stamp: /FangYidong/, licence: 'Apache-2.0',
		 creator: [name: 'Yidong Fang', url: ['https://code.google.com/archive/p/json-simple/']], patched: true, multi: true],
		[file: '../org/zkoss/xel/zel/XelELResolver.java', name: 'apache-tomcat', purl: 'pkg:github/apache/tomcat',
		 version: '2023-04-14T11:51:32+08:00', upstream: null, stamp: /StandardELContext/, licence: 'Apache-2.0',
		 creator: [name: 'The Apache Software Foundation', url: ['https://tomcat.apache.org']], patched: true, multi: true],
		[file: '../org/zkoss/html/HTMLs.java', name: 'owasp-esapi', purl: 'pkg:maven/org.owasp.esapi/esapi',
		 version: '2014-04-11T10:13:42+01:00', upstream: null, stamp: /owasp-esapi-java/, licence: 'BSD-3-Clause',
		 creator: [name: 'The OWASP Foundation', url: ['https://owasp.org/www-project-enterprise-security-api/']], patched: true, multi: true],
	],
	zk : [
		[file: 'web/js/zk/ext/jquery.js', name: 'jquery', npm: 'jquery', stamp: /jQuery JavaScript Library v([\d.]+)/, licence: 'MIT'],
		[file: 'web/js/zk/ext/jquery.json.js', name: 'json2.js', purl: 'pkg:github/douglascrockford/json-js', stamp: /json2\.js\s+([\d-]+)/, licence: 'LicenseRef-scancode-public-domain'],
		[file: 'web/js/zk/ext/jquery.mousewheel.js', name: 'jquery-mousewheel', npm: 'jquery-mousewheel', stamp: /jQuery Mousewheel ([\d.]+)/, licence: 'MIT', dependsOn: ['jquery']],
		[file: 'web/js/zk/ext/jquery.transit.js', name: 'jquery.transit', npm: 'jquery.transit', stamp: /version: "([\d.]+)"/, licence: 'MIT', dependsOn: ['jquery']],
		[file: 'web/js/zk/ext/moment.js', name: 'moment', npm: 'moment', stamp: /\/\/! version : ([\d.]+)/, licence: 'MIT'],
		[file: 'web/js/zk/ext/moment-timezone-with-data.js', name: 'moment-timezone', npm: 'moment-timezone', stamp: /\/\/! version : ([\d.]+)/, licence: 'MIT', dependsOn: ['moment']],
		[file: 'web/js/zk/ext/purify.js', name: 'dompurify', npm: 'dompurify', stamp: /DOMPurify ([\d.]+)/, licence: 'MPL-2.0 OR Apache-2.0'],
		[file: 'web/js/zk/ext/fetch.js', name: 'whatwg-fetch', npm: 'whatwg-fetch', stamp: /github\/fetch v([\d.]+)/, licence: 'MIT'],
		[file: 'web/js/zk/ext/focus-options-polyfill.js', name: 'focus-options-polyfill', npm: 'focus-options-polyfill', version: '1.6.0',
		 sha256: '4df523d51145e241ab3d5e477c6177b6219644c4665701bca62b9d6e3bed7b36', licence: 'MIT'],
		[file: 'web/js/zk/ext/inputdevicecapabilities-polyfill.js', name: 'inputdevicecapabilities-polyfill', purl: 'pkg:github/wicg/inputdevicecapabilities', version: '2021-07-20T10:44:43+08:00', upstream: null,
		 sha256: '48a9b0cfd694821f55f7e39df3dbe7209df0e5a1dbd18db43b0fd7eed78984fe', licence: 'W3C-20150513'],
		[file: 'web/js/zk/ext/promise-polyfill.js', name: 'promise-polyfill', npm: 'promise-polyfill', version: '8.1.3',
		 sha256: 'be183b598a01f2a5b1c45fa280c503300b64e96ce33d22517d9e184486dde736', licence: 'MIT'],
		[file: 'web/js/zk/canvas/ext/excanvas.js', name: 'excanvas', npm: 'excanvas', version: '1.0.0',
		 sha256: 'de4dab1d731b5ac984527fff85bd213db7996c68d61aebe98b121c6614fc4da1', licence: 'Apache-2.0'],
		// copied into js.ts outside ext/, so the scan below misses them; multi: the file and its hash are ZK's
		[file: 'web/js/zk/js.ts', name: 'string.prototype.startswith', npm: 'string.prototype.startswith', stamp: /mths\.be\/startswith v([\d.]+)/, licence: 'MIT',
		 creator: [name: 'Mathias Bynens', url: ['https://mths.be']], multi: true],
		[file: 'web/js/zk/js.ts', name: 'string.prototype.endswith', npm: 'string.prototype.endswith', stamp: /mths\.be\/endswith v([\d.]+)/, licence: 'MIT',
		 creator: [name: 'Mathias Bynens', url: ['https://mths.be']], multi: true],
		[file: 'web/js/zk/js.ts', name: 'weakmap-polyfill', npm: 'weakmap-polyfill', stamp: /weakmap-polyfill v([\d.]+)/, licence: 'MIT', multi: true],
		[file: 'web/js/zk/js.ts', name: 'string.polyfill.js', purl: 'pkg:github/behnammodi/polyfill', stamp: /github\.com\/behnammodi\/polyfill/, licence: 'MIT',
		 creator: [name: 'Behnam Mohammadi', url: ['https://github.com/behnammodi/polyfill']], multi: true],
		[file: '../org/zkoss/zk/au/http/ServletRequestContext.java', name: 'commons-fileupload', purl: 'pkg:maven/commons-fileupload/commons-fileupload',
		 version: '2021-06-17T12:00:36+08:00', upstream: null, stamp: /Licensed to the Apache Software Foundation/, licence: 'Apache-2.0',
		 creator: [name: 'The Apache Software Foundation', url: ['https://commons.apache.org/proper/commons-fileupload/']], patched: true, multi: true],
		[file: '../org/zkoss/zk/ui/sys/DigestUtilsHelper.java', name: 'commons-codec', purl: 'pkg:maven/commons-codec/commons-codec',
		 version: '2017-12-21T15:00:29+08:00', upstream: '1.8', stamp: /DigestUtils/, licence: 'Apache-2.0',
		 creator: [name: 'The Apache Software Foundation', url: ['https://commons.apache.org/proper/commons-codec/']], patched: true, multi: true],
		[file: 'web/js/zk/drag.ts', name: 'script.aculo.us', purl: 'pkg:github/madrobby/scriptaculous', version: '2025-06-24T14:46:20+08:00', upstream: '1.7.0',
		 stamp: /script\.aculo\.us/, licence: 'MIT', creator: [name: 'Thomas Fuchs', url: ['http://script.aculo.us']], patched: true, multi: true],
	],
	zul: [
		[file: 'web/zul/less/font/fontawesome-webfont.ttf', name: 'font-awesome', npm: 'font-awesome', stamp: /Version ([\d.]+)/, licence: 'OFL-1.1 AND MIT',
		 creator: [name: 'Dave Gandy', url: ['https://fontawesome.com']], multi: true, executable: false],
		[file: 'web/zul/less/_reset.less', name: 'normalize.css', purl: 'pkg:npm/normalize.css',
		 version: '2023-04-21T18:29:33+08:00', upstream: null, stamp: /necolas\/normalize\.css/, licence: 'MIT',
		 creator: [name: 'Nicolas Gallagher and Jonathan Neal', url: ['https://github.com/necolas/normalize.css']], patched: true, multi: true, executable: false],
		[file: 'web/zul/less/_reset.less', name: 'html5-boilerplate', purl: 'pkg:npm/html5-boilerplate',
		 version: '2023-04-21T18:29:33+08:00', upstream: null, stamp: /h5bp\/html5-boilerplate/, licence: 'MIT',
		 creator: [name: 'HTML5 Boilerplate', url: ['https://html5boilerplate.com']], patched: true, multi: true, executable: false],
	],
	zel: [
		[file: '../org/zkoss/zel/ELContext.java', name: 'apache-tomcat', purl: 'pkg:github/apache/tomcat',
		 version: '2025-07-17T15:49:36+08:00', upstream: null, stamp: /Licensed to the Apache Software Foundation/, licence: 'Apache-2.0',
		 creator: [name: 'The Apache Software Foundation', url: ['https://tomcat.apache.org']], patched: true, multi: true],
	],
	zkbind: [
		[file: '../org/zkoss/bind/impl/KeyFactory.java', name: 'cojen', purl: 'pkg:maven/org.cojen/cojen',
		 version: '2018-10-23T15:30:41+08:00', upstream: null, stamp: /Brian S O'Neill/, licence: 'Apache-2.0',
		 creator: [name: "Brian S O'Neill", url: ['https://github.com/cojen/Cojen']], patched: true, multi: true],
		[file: '../org/zkoss/bind/paranamer/Paranamer.java', name: 'paranamer', purl: 'pkg:maven/com.thoughtworks.paranamer/paranamer',
		 version: '2020-04-21T15:22:45+08:00', upstream: null, stamp: /Paul Hammant/, licence: 'BSD-3-Clause',
		 creator: [name: 'Paul Hammant', url: ['https://github.com/paul-hammant/paranamer']], patched: true, multi: true],
		[file: '../org/zkoss/bind/paranamer/BytecodeReadingParanamer.java', name: 'asm', purl: 'pkg:maven/org.ow2.asm/asm',
		 version: '2020-04-21T15:22:45+08:00', upstream: null, stamp: /INRIA, France Telecom/, licence: 'BSD-3-Clause',
		 creator: [name: 'INRIA, France Telecom', url: ['https://asm.ow2.io']], patched: true, multi: true],
	],
]
def sbomIgnored = [:]

def extraFile = setting('sbom.extra')
def extra = extraFile ? new GroovyShell().evaluate(new File(extraFile)) : [:]
sbomCreators += extra.creators ?: [:]
sbomLicences += extra.licences ?: [:]
sbomVendored += extra.vendored ?: [:]
sbomIgnored += extra.ignored ?: [:]
def zkDir = new File(setting('zk.directory', "${project.basedir}/..")).canonicalFile
def cmlDir = extraFile ? new File(extraFile).canonicalFile.parentFile.parentFile : null
def moduleDir = { String name -> [cmlDir, zkDir].findAll().collect { new File(it, name) }.find { new File(it, 'src').isDirectory() } }

def bomFile = new File(project.build.directory, "${setting('outputName')}.json")
def evalFile = new File(project.build.directory, "${setting('outputName')}-eval.json")
// the release driver leaves each published jar inside <module>-<version>-bundle.jar under these directories
def bundles = setting('sbom.bundles')?.with { new File(it) }
def evalBundles = setting('sbom.evalBundles')?.with { new File(it) }
def published = { File dir, String module ->
	// EE-eval and PE-eval also hold <module>-<version>-ee-bundle.jar for the maven ee repo
	def bundle = dir?.listFiles()?.find { it.name ==~ /${Pattern.quote(module)}-\d.*-bundle\.jar/ && !it.name.endsWith('-ee-bundle.jar') }
	if (!bundle) return null
	def version = (bundle.name =~ /^${Pattern.quote(module)}-(.+)-bundle\.jar$/)[0][1]
	def bytes = new ZipFile(bundle).withCloseable { z -> z.getEntry("${module}-${version}.jar")?.with { z.getInputStream(it).bytes } }
	bytes ? [version: version, bytes: bytes] : null
}
def bom = new JsonSlurper().parse(bomFile)
// the plugin writes no compositions, so this is the output of an earlier run: keep it rather than delete it below
if (bom.compositions) throw new IllegalStateException("SBOM: ${bomFile} is already processed; run makeAggregateBom again first")
evalFile.delete()
try {
	def problems = [] as LinkedHashSet
	def isZk = { Map c -> c.group in ['org.zkoss.zk', 'org.zkoss.common'] && c.version == project.version }
	def isOwn = { Map c -> extra.licence && (c.is(bom.metadata.component) || new File(cmlDir, "${c.name}/src").isDirectory()) }
	def digest = { InputStream input ->
		def mds = ['SHA-256', 'SHA-512'].collectEntries { [(it): MessageDigest.getInstance(it)] }
		input.withCloseable { it.eachByte(65536) { byte[] buf, int n -> mds.values()*.update(buf, 0, n) } }
		mds.collect { alg, md -> [alg: alg, content: md.digest().encodeHex().toString()] }
	}
	def fileProps = { String filename, boolean executable, boolean archive ->
		(filename ? [[name: 'bsi:component:filename', value: filename]] : []) + [
			[name: 'bsi:component:executable', value: executable ? 'executable' : 'non-executable'],
			[name: 'bsi:component:archive', value: archive ? 'archive' : 'no archive'],
			[name: 'bsi:component:structured', value: archive ? 'structured' : 'unstructured']]
	}
	// TR-03183-2 reads the deployable's hash from a distribution reference, not from `hashes`
	def addHash = { Map c, String url, List hashes ->
		c.hashes = hashes
		c.externalReferences = (c.externalReferences ?: []).findAll { it.type != 'distribution' } + [[type: 'distribution', url: url, hashes: hashes.findAll { it.alg == 'SHA-512' }]]
	}
	def entryHashes = { byte[] jar, String entry ->
		new ZipInputStream(new ByteArrayInputStream(jar)).withCloseable { zin ->
			for (def e = zin.nextEntry; e; e = zin.nextEntry)
				if (e.name == entry) return digest(zin)
			null
		}
	}
	// a ZK module's hash must be the published jar's, never the local -SNAPSHOT jar the plugin resolved
	def hashPublished = { File dir, Map c, List vendoredFiles ->
		def jar = published(dir, c.name)
		if (!jar) {
			if (dir) throw new IllegalStateException("SBOM: no published jar of ${c.name} under ${dir}")
			c.remove('hashes')
			c.externalReferences = (c.externalReferences ?: []).findAll { it.type != 'distribution' }
			log.warn("SBOM: no published jar of ${c.name} under ${dir}")
			return null
		}
		addHash(c, c.purl, digest(new ByteArrayInputStream(jar.bytes)))
		vendoredFiles.each { v -> v.entryHashes = entryHashes(jar.bytes, v.file) }
		jar.version
	}
	def licenceOf = { Map c ->
		if (isZk(c) || c.is(bom.metadata.component)) return isOwn(c) ? extra.licence : (zkModuleLicences[c.name] ?: zkLicence)
		def key = "${c.group}:${c.name}"
		def listed = sbomLicences["$key:${c.version}"] ?: sbomLicences[key]
		if (listed) return listed
		def ids = c.licenses*.license*.id
		if (ids && !ids.contains(null)) return ids.join(' OR ') // a POM listing several licences offers a choice
		problems << "no SPDX licence for $key; add it to sbomLicences".toString()
		null
	}
	def enrich = { Map c ->
		def creator = sbomCreators.findAll { k, v -> c.group == k || c.group.startsWith("${k}.") }.max { it.key.length() }?.value
		if (creator) {
			c.supplier = creator
			c.manufacturer = creator
		} else problems << "no creator for ${c.group}:${c.name}; add its group to sbomCreators".toString()
		def licence = licenceOf(c)
		if (licence) c.licenses = [[expression: licence, acknowledgement: 'concluded']]
		if (c.purl?.contains('type=jar')) {
			if (!isZk(c) && c.hashes) addHash(c, c.purl, c.hashes)
			c['properties'] = (c['properties'] ?: []) + fileProps("${c.name}-${c.version}.jar", true, true)
		}
	}

	enrich(bom.metadata.component)
	bom.components.each(enrich)

	def vendoredOf = [:]
	def usedRefs = [] as Set
	def released
	bom.components.findAll(isZk).each { owner ->
		def resources = new File(moduleDir(owner.name) ?: new File(zkDir, owner.name), 'src/archive')
		def files = sbomVendored[owner.name] ?: []
		def listed = (files*.file + files*.also.flatten().findAll() + (sbomIgnored[owner.name] ?: [])) as Set
		def web = new File(resources, 'web')
		if (web.isDirectory())
			web.eachFileRecurse(FileType.FILES) { f ->
				def path = resources.toURI().relativize(f.toURI()).path
				if (path.contains('/ext/') && !f.name.startsWith('.') && !listed.any { it == path || (it.endsWith('/') && path.startsWith(it)) })
					problems << "${owner.name}: ${path} is not listed in sbomVendored".toString()
			}
		def resolved = files.collectMany { v ->
			def src = new File(resources, v.file)
			def version = v.version
			if (v.stamp) {
				def m = src.getText('ISO-8859-1') =~ v.stamp
				if (m.find()) version = m.groupCount() ? m.group(1) : version // a stamp without a group only checks the copy is still there
				else problems << "${owner.name}: ${v.file} no longer matches ${v.stamp}".toString()
			} else if (src.bytes.sha256() != v.sha256)
				problems << "${owner.name}: ${v.file} changed; check its version, then update its sha256".toString()
			def purl = v.npm ? "pkg:npm/${v.npm}@${version}".toString() : v.purl
			// the same upstream copied into two modules keeps one purl, so the second copy takes a ref of its own
			def r = v + [version: version, purl: purl, ref: usedRefs.add(purl) ? purl : "${owner.purl}#${v.name}".toString()]
			// an `also` file belongs to the same library: same purl, but its own filename and hash
			[r] + (v.multi ? [] : (v.also ?: []).collect { f -> r + [file: f, ref: "${owner.purl}#${f}".toString(), dependsOn: null, executable: f ==~ /.+\.m?js/] })
		}
		def version = owner.purl?.contains('type=jar') ? hashPublished(bundles, owner, resolved) : null
		released = released ?: version
		resolved.each { v ->
			def patched = v.patched || !v.creator
			def creator = patched ? potix : v.creator
			def c = [type: 'library', 'bom-ref': v.ref, supplier: creator, manufacturer: creator, name: v.name, version: v.version, purl: v.purl,
			         licenses: [[expression: v.licence, acknowledgement: 'concluded']]]
			if (!v.version) c.remove('version') // no upstream release to name, e.g. a copy taken before upstream tagged one
			// an entry versioned by the date of Potix's last commit names the upstream release it came from in `upstream`, null when there is none
			def upstream = v.containsKey('upstream') ? v.upstream : v.version
			if (patched) c.pedigree = [
				ancestors: [[type: 'library', name: v.name] + (upstream ? [version: upstream] : [:]) + [purl: v.purl] + (v.creator ? [supplier: v.creator] : [:])],
				patches  : [[type: 'unofficial']],
				notes    : 'Copy bundled in ZK and patched for it, maintained by Potix' + (upstream == v.version ? '; keeps the upstream version so advisories still match' : '')]
			// a component of several files has no single filename or hash
			c['properties'] = fileProps(v.multi ? null : new File(v.file).name, v.executable != false, false)
			if (!v.multi && v.entryHashes) addHash(c, "${owner.purl}#${v.file}".toString(), v.entryHashes)
			bom.components << c
		}
		def refs = resolved.collectEntries { [(it.name): it.purl] }
		resolved.each { v -> bom.dependencies << [ref: v.ref, dependsOn: (v.dependsOn ?: []).collect { refs[it] }] }
		if (resolved) bom.dependencies.find { it.ref == owner.'bom-ref' }.dependsOn += resolved*.ref
		vendoredOf[owner.'bom-ref'] = resolved
	}

	// cmldoc/release leaves zk-parent and zkcml-parent at the release version, so isZk matches no module until the poms are restored
	if (bundles && !released)
		throw new IllegalStateException("SBOM: no ZK module in ${bomFile} has version ${project.version}; restore the poms cmldoc/release rewrote, then run makeAggregateBom again")
	if (problems)
		throw new IllegalStateException("SBOM: update sbom.groovy:\n  ${problems.join('\n  ')}")
	bom.metadata.lifecycles = [[phase: 'build']]
	bom.metadata.manufacturer = potix
	bom.metadata.supplier = potix
	bom.compositions = [[aggregate: 'complete', dependencies: bom.dependencies*.ref]]
	def text = JsonOutput.prettyPrint(JsonOutput.toJson(bom))
	// the restored 9.x poms are at -SNAPSHOT; the jars ship as `released`
	def publish = { File f, String json -> f.text = released ? json.replace(project.version, released) : json }
	publish(bomFile, text)

	// EE Eval jars differ from the official ones, so the Eval SBOM carries their hashes
	if (evalBundles && evalBundles.canonicalPath != bundles?.canonicalPath) {
		def eval = new JsonSlurper().parseText(text)
		eval.serialNumber = "urn:uuid:${UUID.randomUUID()}".toString()
		// the Eval distribution ships zkcml's cmldoc/LICENSE, a trial agreement, instead of the official licence
		if (isOwn(bom.metadata.component))
			([eval.metadata.component] + eval.components.findAll(isOwn)).each { it.licenses = [[expression: extra.evalLicence, acknowledgement: 'concluded']] }
		eval.components.findAll { isZk(it) && isOwn(it) && it.purl?.contains('type=jar') }.each { c ->
			def files = (vendoredOf[c.'bom-ref'] ?: []).collect { it + [entryHashes: null] }
			hashPublished(evalBundles, c, files)
			files.findAll { it.entryHashes && !it.multi }.each { v ->
				addHash(eval.components.find { it.'bom-ref' == v.ref }, "${c.purl}#${v.file}".toString(), v.entryHashes)
			}
		}
		publish(evalFile, JsonOutput.prettyPrint(JsonOutput.toJson(eval)))
	}
} catch (e) {
	bomFile.delete() // never ship the plugin's raw output in place of the enriched one
	throw e
}
