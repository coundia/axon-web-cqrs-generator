# add to side panel
 src/app/shared/side-panel/side-panel.component.html
```
<app-side-link
	link="/setting"
	label="Setting"
	[icon]="'M3 13h2v-2H3v2zm0-4h2V7H3v2zm0 8h2v-2H3v2zm4 0h14V5H7v14z'"
	(clicked)="toggleMenu()"
/>
```
# add   route
src/app/app.routes.ts
```
export const routes: Routes = [
...settingRoutes,
// other routes

]
```

## add translation
src/app/shared/translation/httpLoaderFactory.ts
```
const TRANSLATION_FILES = [
	'setting',
	//other translation files
];
```

## dont forget to add import