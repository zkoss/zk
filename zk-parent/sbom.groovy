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
		 sha256: 'ad21eb33cb3115982dc6edb960646393dd2f53134530da83e84cc5d8ea51ce1e', licence: 'MIT'],
		[file: 'web/js/zk/ext/inputdevicecapabilities-polyfill.js', name: 'inputdevicecapabilities-polyfill', purl: 'pkg:github/wicg/inputdevicecapabilities', version: '2021-07-20T10:44:43+08:00',
		 sha256: '7e56ccd5dd3e1c0eab0cf0ac22925ee33763ad6346bf03f2770c2a581db9117d', licence: 'W3C-20150513'],
		[file: 'web/js/zk/ext/promise-polyfill.js', name: 'promise-polyfill', npm: 'promise-polyfill', version: '8.1.3',
		 sha256: '0a97670c97738766fd4ddf38da6389060cc6d396824b8e4837257aaff3310128', licence: 'MIT'],
		[file: 'web/js/zk/canvas/ext/excanvas.js', name: 'excanvas', npm: 'excanvas', version: '1.0.0',
		 sha256: 'de4dab1d731b5ac984527fff85bd213db7996c68d61aebe98b121c6614fc4da1', licence: 'Apache-2.0'],
	],
	zul: [
		[file: 'web/zul/less/font/fontawesome-webfont.ttf', name: 'font-awesome', npm: 'font-awesome', stamp: /Version ([\d.]+)/, licence: 'OFL-1.1 AND MIT',
		 creator: [name: 'Dave Gandy', url: ['https://fontawesome.com']], multi: true, executable: false],
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
	def bundle = dir?.listFiles()?.find { it.name ==~ /${Pattern.quote(module)}-\d.*-bundle\.jar/ }
	if (!bundle) return null
	def version = (bundle.name =~ /^${Pattern.quote(module)}-(.+)-bundle\.jar$/)[0][1]
	def bytes = new ZipFile(bundle).withCloseable { z -> z.getEntry("${module}-${version}.jar")?.with { z.getInputStream(it).bytes } }
	bytes ? [version: version, bytes: bytes] : null
}
evalFile.delete()
try {
	def bom = new JsonSlurper().parse(bomFile)
	def problems = [] as LinkedHashSet
	def isZk = { Map c -> c.group in ['org.zkoss.zk', 'org.zkoss.common'] && c.version == project.version }
	def isOwn = { Map c -> extra.licence && (c.is(bom.metadata.component) || moduleDir(c.name)?.path?.startsWith(cmlDir.path)) }
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
	def released = setting('sbom.version')
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
		def resolved = files.collect { v ->
			def src = new File(resources, v.file)
			def version = v.version
			if (v.stamp) {
				def m = src.getText('ISO-8859-1') =~ v.stamp
				if (m.find()) version = m.group(1)
				else problems << "${owner.name}: ${v.file} no longer matches ${v.stamp}".toString()
			} else if (src.bytes.sha256() != v.sha256)
				problems << "${owner.name}: ${v.file} changed; check its version, then update its sha256".toString()
			v + [version: version, purl: v.npm ? "pkg:npm/${v.npm}@${version}".toString() : v.purl]
		}
		def version = owner.purl?.contains('type=jar') ? hashPublished(bundles, owner, resolved) : null
		released = released ?: version
		resolved.each { v ->
			def creator = v.creator ?: potix
			def c = [type: 'library', 'bom-ref': v.purl, supplier: creator, manufacturer: creator, name: v.name, version: v.version, purl: v.purl,
			         licenses: [[expression: v.licence, acknowledgement: 'concluded']]]
			if (!v.creator) c.pedigree = [
				ancestors: [[type: 'library', name: v.name, version: v.version, purl: v.purl]],
				patches  : [[type: 'unofficial']],
				notes    : 'Copy bundled in ZK and patched for it, maintained by Potix; keeps the upstream version so advisories still match']
			// a component of several files has no single filename or hash
			c['properties'] = fileProps(v.multi ? null : new File(v.file).name, v.executable != false, false)
			if (!v.multi && v.entryHashes) addHash(c, "${owner.purl}#${v.file}".toString(), v.entryHashes)
			bom.components << c
		}
		def refs = resolved.collectEntries { [(it.name): it.purl] }
		resolved.each { v -> bom.dependencies << [ref: v.purl, dependsOn: (v.dependsOn ?: []).collect { refs[it] }] }
		if (resolved) bom.dependencies.find { it.ref == owner.'bom-ref' }.dependsOn += resolved*.purl
		vendoredOf[owner.'bom-ref'] = resolved
	}

	if (problems)
		throw new IllegalStateException("SBOM: update sbom.groovy:\n  ${problems.join('\n  ')}")
	bom.metadata.lifecycles = [[phase: 'build']]
	bom.metadata.manufacturer = potix
	bom.metadata.supplier = potix
	bom.compositions = [[aggregate: 'complete', dependencies: bom.dependencies*.ref]]
	def text = JsonOutput.prettyPrint(JsonOutput.toJson(bom))
	// 9.x poms stay at the -SNAPSHOT version during an FL build; the jars ship as `released`
	def publish = { File f, String json -> f.text = released ? json.replace(project.version, released) : json }
	publish(bomFile, text)

	// EE Eval jars differ from the official ones, so the Eval SBOM carries their hashes
	if (evalBundles && evalBundles.canonicalPath != bundles?.canonicalPath) {
		def eval = new JsonSlurper().parseText(text)
		eval.serialNumber = "urn:uuid:${UUID.randomUUID()}".toString()
		eval.components.findAll { isZk(it) && isOwn(it) && it.purl?.contains('type=jar') }.each { c ->
			def files = (vendoredOf[c.'bom-ref'] ?: []).collect { it + [entryHashes: null] }
			hashPublished(evalBundles, c, files)
			files.findAll { it.entryHashes && !it.multi }.each { v ->
				addHash(eval.components.find { it.'bom-ref' == v.purl }, "${c.purl}#${v.file}".toString(), v.entryHashes)
			}
		}
		publish(evalFile, JsonOutput.prettyPrint(JsonOutput.toJson(eval)))
	}
} catch (e) {
	bomFile.delete() // never ship the plugin's raw output in place of the enriched one
	throw e
}
