<map version="freeplane 1.6.0">
<!--To view this file, download free mind mapping software Freeplane from http://freeplane.sourceforge.net -->
<node TEXT="Chizuru" LOCALIZED_STYLE_REF="AutomaticLayout.level.root" FOLDED="false" ID="ID_1865421036" CREATED="1508117575126" MODIFIED="1508335182648"><hook NAME="MapStyle" zoom="1.1824832">
    <conditional_styles>
        <conditional_style ACTIVE="false" STYLE_REF="Revision" LAST="false">
            <time_condition_modified_after user_name="1.2.12_03" DATE="1330531663397"/>
        </conditional_style>
    </conditional_styles>
    <properties show_icon_for_attributes="false" fit_to_viewport="false;" show_note_icons="true" show_notes_in_map="false"/>

<map_styles>
<stylenode LOCALIZED_TEXT="styles.root_node" STYLE="oval" UNIFORM_SHAPE="true" VGAP_QUANTITY="24.0 pt">
<font SIZE="24"/>
<stylenode LOCALIZED_TEXT="styles.user-defined" POSITION="right" STYLE="bubble">
<stylenode TEXT="Definition">
<font ITALIC="true"/>
<edge STYLE="bezier" COLOR="#808080"/>
</stylenode>
<stylenode TEXT="Method">
<edge COLOR="#33ffcc"/>
</stylenode>
<stylenode TEXT="OptionalValue" COLOR="#cc3300">
<edge COLOR="#33ffcc"/>
</stylenode>
<stylenode TEXT="Procedure" COLOR="#006666">
<font BOLD="true"/>
<edge COLOR="#808080"/>
</stylenode>
<stylenode TEXT="Exception">
<icon BUILTIN="messagebox_warning"/>
<edge STYLE="bezier" COLOR="#808080"/>
</stylenode>
<stylenode TEXT="Refine">
<icon BUILTIN="xmag"/>
<edge STYLE="bezier" COLOR="#808080"/>
</stylenode>
<stylenode TEXT="ToNote">
<icon BUILTIN="yes"/>
<edge STYLE="bezier" COLOR="#808080"/>
</stylenode>
<stylenode TEXT="Example">
<icon BUILTIN="../AttributesView"/>
<edge STYLE="bezier" COLOR="#808080"/>
</stylenode>
<stylenode TEXT="MainMenu" BACKGROUND_COLOR="#33ffcc" STYLE="bubble">
<font BOLD="true"/>
<edge STYLE="horizontal" COLOR="#33ffcc"/>
</stylenode>
<stylenode TEXT="SubMenu" BACKGROUND_COLOR="#33ffcc" STYLE="bubble">
<edge COLOR="#33ffcc"/>
</stylenode>
<stylenode TEXT="MenuGroupLabel" COLOR="#000000" BACKGROUND_COLOR="#33ffcc" STYLE="bubble">
<edge COLOR="#33ffcc" WIDTH="2"/>
</stylenode>
<stylenode TEXT="Title" COLOR="#ffffff" BACKGROUND_COLOR="#2ed2a9" STYLE="bubble">
<font BOLD="true"/>
<edge STYLE="horizontal"/>
</stylenode>
<stylenode TEXT="IsChecked" COLOR="#cc3300">
<icon BUILTIN="checked"/>
<edge COLOR="#33ffcc"/>
</stylenode>
<stylenode TEXT="UnChecked" COLOR="#cc3300">
<icon BUILTIN="unchecked"/>
<edge COLOR="#33ffcc"/>
</stylenode>
<stylenode TEXT="Revision">
<icon BUILTIN="revision"/>
</stylenode>
<stylenode TEXT="UserGuide">
<edge STYLE="horizontal"/>
</stylenode>
<stylenode TEXT="ProcedureStep">
<edge STYLE="bezier"/>
</stylenode>
<stylenode LOCALIZED_TEXT="styles.topic" COLOR="#18898b" STYLE="fork">
<font NAME="Liberation Sans" SIZE="12" BOLD="true"/>
</stylenode>
<stylenode LOCALIZED_TEXT="styles.subtopic" COLOR="#cc3300" STYLE="fork">
<font NAME="Liberation Sans" SIZE="12" BOLD="true"/>
</stylenode>
<stylenode LOCALIZED_TEXT="styles.subsubtopic" COLOR="#669900">
<font NAME="Liberation Sans" SIZE="12" BOLD="true"/>
</stylenode>
<stylenode LOCALIZED_TEXT="styles.important">
<icon BUILTIN="yes"/>
</stylenode>
</stylenode>
<stylenode LOCALIZED_TEXT="styles.predefined" POSITION="right" STYLE="bubble">
<stylenode LOCALIZED_TEXT="default" FORMAT_AS_HYPERLINK="true">
<edge COLOR="#808080" WIDTH="thin"/>
<font SIZE="8"/>
</stylenode>
<stylenode LOCALIZED_TEXT="defaultstyle.details"/>
<stylenode LOCALIZED_TEXT="defaultstyle.attributes">
<font SIZE="9"/>
</stylenode>
<stylenode LOCALIZED_TEXT="defaultstyle.note">
<edge COLOR="#cc0000"/>
</stylenode>
<stylenode LOCALIZED_TEXT="defaultstyle.floating">
<edge STYLE="hide_edge"/>
<cloud COLOR="#f0f0f0" SHAPE="ROUND_RECT"/>
</stylenode>
</stylenode>
<stylenode LOCALIZED_TEXT="styles.AutomaticLayout" POSITION="right" STYLE="bubble">
<stylenode LOCALIZED_TEXT="AutomaticLayout.level.root" COLOR="#000000">
<font SIZE="20"/>
</stylenode>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,1" COLOR="#0033ff">
<font SIZE="18"/>
</stylenode>
</stylenode>
</stylenode>
</map_styles>
</hook>
<node TEXT="Improvement" STYLE_REF="MainMenu" POSITION="right" ID="ID_1612436428" CREATED="1508335160413" MODIFIED="1508335643241">
<node TEXT="Range Fields Involvement" STYLE_REF="SubMenu" ID="ID_1147873843" CREATED="1508119118335" MODIFIED="1508335639081" HGAP_QUANTITY="43.2499991282821 pt" VSHIFT_QUANTITY="44.24999868124727 pt">
<node TEXT="Fields Design" STYLE_REF="Title" ID="ID_1571371407" CREATED="1508335382527" MODIFIED="1508335565158">
<node TEXT="Range-Start" LOCALIZED_STYLE_REF="default" ID="ID_917982318" CREATED="1508335451513" MODIFIED="1508335585929"/>
<node TEXT="Range-End" ID="ID_1258828782" CREATED="1508335568070" MODIFIED="1508335572578"/>
</node>
<node TEXT="Related Table" STYLE_REF="Title" ID="ID_382442618" CREATED="1508335533509" MODIFIED="1508335560275">
<node TEXT="Chunk Table" ID="ID_85978508" CREATED="1508335592480" MODIFIED="1508335595146"/>
<node TEXT="SubResource Table" ID="ID_258848084" CREATED="1508335597647" MODIFIED="1508335601875"/>
</node>
</node>
<node TEXT="Accelerate Table for&#xa;AmassedResource" STYLE_REF="Title" ID="ID_98878845" CREATED="1508117579839" MODIFIED="1508335643241" HGAP_QUANTITY="47.74999899417165 pt" VSHIFT_QUANTITY="-1.4999999552965164 pt">
<edge COLOR="#ff0000"/>
<node TEXT="Check and save whether ChunkResources are&#xa;in the same size" ID="ID_501298085" CREATED="1508117909872" MODIFIED="1508119393149" HGAP_QUANTITY="39.49999924004081 pt" VSHIFT_QUANTITY="-9.749999709427364 pt"/>
<node TEXT="If ChunkResources are not at same size, use&#xa;the range fields in Resource Table." ID="ID_1856345654" CREATED="1508118009579" MODIFIED="1508119394341" HGAP_QUANTITY="31.99999946355821 pt" VSHIFT_QUANTITY="13.499999597668658 pt"/>
</node>
<node TEXT="Involve DDDSample Project architecture" STYLE_REF="Title" ID="ID_1819892199" CREATED="1508326785863" MODIFIED="1508335635598" HGAP_QUANTITY="46.99999901652339 pt" VSHIFT_QUANTITY="-56.249998323619415 pt">
<node TEXT="Jdon Framework" ID="ID_527520069" CREATED="1508335757863" MODIFIED="1508336091769">
<node TEXT="https://github.com/banq/jdonframework" ID="ID_1374759068" CREATED="1508336074627" MODIFIED="1508336074627" LINK="https://github.com/banq/jdonframework"/>
<node TEXT="CQRS" ID="ID_1748572059" CREATED="1508336498634" MODIFIED="1508336502399"/>
</node>
<node TEXT="https://www.reactivemanifesto.org/" ID="ID_1424243734" CREATED="1508336474828" MODIFIED="1508336474828" LINK="https://www.reactivemanifesto.org/"/>
</node>
<node TEXT="ApplicationContext v.s SpringContext" STYLE_REF="Title" ID="ID_1959609052" CREATED="1508938512977" MODIFIED="1508938655387">
<node TEXT="Use SpringContext instead of ApplicationContext?" ID="ID_949446875" CREATED="1508938650586" MODIFIED="1508939052919">
<node TEXT="Current Elements consisted in ApplicationContext" STYLE_REF="ToNote" ID="ID_1724888776" CREATED="1508938746843" MODIFIED="1508939143280" HGAP_QUANTITY="-188.4999939650299 pt" VSHIFT_QUANTITY="35.249998949468164 pt">
<node TEXT="Application Configuration" ID="ID_1698184988" CREATED="1508939035653" MODIFIED="1508939143279" HGAP_QUANTITY="13.999999999999996 pt" VSHIFT_QUANTITY="-5.249999843537808 pt"/>
<node TEXT="EntityManager with all Repositories packed" ID="ID_362385030" CREATED="1508939076856" MODIFIED="1508939140450"/>
</node>
</node>
<node TEXT="Get SpringContext through Channel ?" ID="ID_137728455" CREATED="1508939192116" MODIFIED="1508939210691"/>
</node>
<node TEXT="Alter SubResource Table" STYLE_REF="Title" ID="ID_102464200" CREATED="1508942918945" MODIFIED="1508943017714">
<node TEXT="ID" ID="ID_1969850633" CREATED="1508942950018" MODIFIED="1508942970490"/>
<node TEXT="key" ID="ID_883933699" CREATED="1508942971848" MODIFIED="1508942973460">
<node TEXT="rename to index_in_parent" STYLE_REF="Refine" ID="ID_1046224255" CREATED="1508943027204" MODIFIED="1508943111692"/>
</node>
<node TEXT="parent_version_id" ID="ID_1490306016" CREATED="1508942973639" MODIFIED="1508942977290"/>
<node TEXT="etag" ID="ID_142695582" CREATED="1508942977398" MODIFIED="1508942980050"/>
<node TEXT="last_modified" ID="ID_61712150" CREATED="1508942980198" MODIFIED="1508942984040"/>
<node TEXT="size" ID="ID_524139085" CREATED="1508942984429" MODIFIED="1508942986271"/>
<node TEXT="storage_class" ID="ID_350318095" CREATED="1508942986788" MODIFIED="1508942989590"/>
<node TEXT="version_id" ID="ID_1296245964" CREATED="1508942989839" MODIFIED="1508942992750"/>
</node>
<node TEXT="Build Up Netty Server via Spring" STYLE_REF="Title" ID="ID_165513159" CREATED="1509064778001" MODIFIED="1509064814432"/>
</node>
</node>
</map>
