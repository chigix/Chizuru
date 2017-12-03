<map version="freeplane 1.6.0">
<!--To view this file, download free mind mapping software Freeplane from http://freeplane.sourceforge.net -->
<node TEXT="Chizuru" LOCALIZED_STYLE_REF="AutomaticLayout.level.root" FOLDED="false" ID="ID_1865421036" CREATED="1508117575126" MODIFIED="1508335182648"><hook NAME="MapStyle" zoom="1.197967">
    <conditional_styles>
        <conditional_style ACTIVE="false" STYLE_REF="Revision" LAST="false">
            <time_condition_modified_after user_name="1.2.12_03" DATE="1330531663397"/>
        </conditional_style>
    </conditional_styles>
    <properties fit_to_viewport="false;" show_icon_for_attributes="false" show_note_icons="true" show_notes_in_map="false"/>

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
<node TEXT="Improvement" STYLE_REF="MainMenu" POSITION="right" ID="ID_1612436428" CREATED="1508335160413" MODIFIED="1512270954987">
<node TEXT="Accelerate Table for&#xa;AmassedResource" STYLE_REF="Title" ID="ID_98878845" CREATED="1508117579839" MODIFIED="1508335643241" HGAP_QUANTITY="47.74999899417165 pt" VSHIFT_QUANTITY="-1.4999999552965164 pt">
<edge COLOR="#ff0000"/>
<node TEXT="Check and save whether ChunkResources are&#xa;in the same size" ID="ID_501298085" CREATED="1508117909872" MODIFIED="1508119393149" HGAP_QUANTITY="39.49999924004081 pt" VSHIFT_QUANTITY="-9.749999709427364 pt"/>
<node TEXT="If ChunkResources are not at same size, use&#xa;the range fields in Resource Table." ID="ID_1856345654" CREATED="1508118009579" MODIFIED="1508119394341" HGAP_QUANTITY="31.99999946355821 pt" VSHIFT_QUANTITY="13.499999597668658 pt"/>
<node TEXT="Alter Table `Resource` to add column named as `segment_size_support`" ID="ID_357758292" CREATED="1510899153711" MODIFIED="1510899235933"/>
</node>
<node TEXT="Involve DDDSample Project architecture" STYLE_REF="Title" ID="ID_1819892199" CREATED="1508326785863" MODIFIED="1512270947160" HGAP_QUANTITY="41.73972518136142 pt" VSHIFT_QUANTITY="5.558219239533749 pt">
<node TEXT="Jdon Framework" ID="ID_527520069" CREATED="1508335757863" MODIFIED="1508336091769">
<node TEXT="https://github.com/banq/jdonframework" ID="ID_1374759068" CREATED="1508336074627" MODIFIED="1508336074627" LINK="https://github.com/banq/jdonframework"/>
<node TEXT="CQRS" ID="ID_1748572059" CREATED="1508336498634" MODIFIED="1508336502399"/>
</node>
<node TEXT="https://www.reactivemanifesto.org/" ID="ID_1424243734" CREATED="1508336474828" MODIFIED="1508336474828" LINK="https://www.reactivemanifesto.org/"/>
</node>
<node TEXT="POST Resource API support" ID="ID_1094277261" CREATED="1512270913654" MODIFIED="1512270954982" VSHIFT_QUANTITY="-1.643835573488115 pt"/>
<node TEXT="ApplicationContext v.s SpringContext" STYLE_REF="Title" ID="ID_1959609052" CREATED="1508938512977" MODIFIED="1512270950832" HGAP_QUANTITY="14.657534229395246 pt" VSHIFT_QUANTITY="0.3287671146976232 pt">
<node TEXT="Use SpringContext instead of ApplicationContext?" ID="ID_949446875" CREATED="1508938650586" MODIFIED="1508939052919">
<node TEXT="Current Elements consisted in ApplicationContext" STYLE_REF="ToNote" ID="ID_1724888776" CREATED="1508938746843" MODIFIED="1508939143280" HGAP_QUANTITY="-188.4999939650299 pt" VSHIFT_QUANTITY="35.249998949468164 pt">
<node TEXT="Application Configuration" ID="ID_1698184988" CREATED="1508939035653" MODIFIED="1508939143279" HGAP_QUANTITY="13.999999999999996 pt" VSHIFT_QUANTITY="-5.249999843537808 pt"/>
<node TEXT="EntityManager with all Repositories packed" ID="ID_362385030" CREATED="1508939076856" MODIFIED="1508939140450"/>
</node>
</node>
<node TEXT="Get SpringContext through Channel ?" ID="ID_137728455" CREATED="1508939192116" MODIFIED="1508939210691"/>
<node TEXT="ApplicationContext is not needed in Testing Env" ID="ID_828071382" CREATED="1511080504803" MODIFIED="1511080535063"/>
</node>
<node TEXT="Build Up Netty Server via Spring" STYLE_REF="Title" ID="ID_165513159" CREATED="1509064778001" MODIFIED="1509064814432"/>
</node>
</node>
</map>
