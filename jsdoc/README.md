# TypeScript Declarations for the ZK Framework

## Demo
```sh
npm i -D typescript zk-types
npx tsc --init
```
Modify `tsconfig.json` to have:
```json
{
    "compilerOptions": {
        "types": ["zk-types"]
    }
}
```
Create a TS file with whatever name (e.g., `demo.ts`) and start typing:
```ts
const oldPanel = zk.augment(zul.wnd.Panel.prototype, {
    onClose(): void {
        alert('Modified zul.wnd.Panel.prototype.onClose');
        return oldPanel.onClose();
    }
})
```
Compile with:
```sh
npx tsc
```