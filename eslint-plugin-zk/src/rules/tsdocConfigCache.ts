// Adapted from tsdoc/eslint-plugin/src/ConfigCache.ts
import { TSDocConfigFile } from '@microsoft/tsdoc-config';
import * as path from 'path';

interface ICachedConfig {
	loadTimeMs: number;
	lastCheckTimeMs: number;
	configFile: TSDocConfigFile;
}

// How often to check for modified input files. Evict the cache entry when a file's modification timestamp has changed.
const CACHE_CHECK_INTERVAL_MS = 3_000;
// Evict old entries from the cache after this much time, regardless of whether the file was detected as being modified or not.
const CACHE_EXPIRE_MS = 20_000;
// If this many objects accumulate in the cache, then it is cleared to avoid a memory leak.
const CACHE_MAX_SIZE = 100;
// configFilePath -> config
const cachedConfigs = new Map<string, ICachedConfig>();

/**
 * Node.js equivalent of performance.now().
 */
function getTimeInMs(): number {
	const [seconds, nanoseconds] = process.hrtime();
	return seconds * 1_000 + nanoseconds / 1_000_000;
}

export function getForSourceFile(sourceFilePath: string): TSDocConfigFile {
	const sourceFileFolder = path.dirname(path.resolve(sourceFilePath));
	// First, determine the file to be loaded. If not found, the configFilePath will be an empty string.
	const configFilePath = TSDocConfigFile.findConfigPathForFolder(sourceFileFolder);
	// If configFilePath is an empty string, then we'll use the folder of sourceFilePath as our cache key.
	const cacheKey = configFilePath || sourceFileFolder + '/';
	const nowMs = getTimeInMs();

	let cachedConfig = cachedConfigs.get(cacheKey);
	if (cachedConfig) {
		const loadAgeMs = nowMs - cachedConfig.loadTimeMs;
		const lastCheckAgeMs = nowMs - cachedConfig.lastCheckTimeMs;

		// Is the cached object still valid?
		if (loadAgeMs < 0 || CACHE_EXPIRE_MS < loadAgeMs) {
			cachedConfig = undefined;
			cachedConfigs.delete(cacheKey);
		} else if (lastCheckAgeMs < 0 || CACHE_CHECK_INTERVAL_MS < lastCheckAgeMs) {
			cachedConfig.lastCheckTimeMs = nowMs;
			if (cachedConfig.configFile.checkForModifiedFiles()) {
				// Invalidate the cache because it failed to load completely
				cachedConfig = undefined;
				cachedConfigs.delete(cacheKey);
			}
		}
	}

	// Load the object
	if (!cachedConfig) {
		if (cachedConfigs.size > CACHE_MAX_SIZE) {
			cachedConfigs.clear(); // avoid a memory leak
		}
		cachedConfig = {
			configFile: TSDocConfigFile.loadFile(configFilePath),
			lastCheckTimeMs: nowMs,
			loadTimeMs: nowMs
		};
		cachedConfigs.set(cacheKey, cachedConfig);
	}

	return cachedConfig.configFile;
}
