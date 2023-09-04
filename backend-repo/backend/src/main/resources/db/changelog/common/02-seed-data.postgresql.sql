-- liquibase formatted sql

--changeset sathyam:02-seed-data-1
INSERT into addresses (id, created_at, modified_at, city, country, line1, line2, state, pincode)
VALUES (1, 0, 0, 'maharashtra', 'India', 'Ganapatrao Kadam Marg', 'Lower Parel West', 'Mumbai', 400013);

--changeset sathyam:02-seed-data-2
INSERT INTO organisations (id, addresses_id, contact_number, deleted_status, created_at, modified_at)
VALUES (1, 1, 'Leucine', false, 0, 0);

--changeset sathyam:02-seed-data-3
INSERT INTO users (id, created_at, modified_at, organisations_id, employee_id, email, first_name, is_verified, is_archived, username)
VALUES (1, 0, 0, 1, 'SYSTEM', 'system@leucinetech.com', 'System', false, true, 'system');

--changeset sathyam:02-seed-data-4
INSERT INTO email_templates(id, name, content)
VALUES (1, 'USER_UNASSIGNED_FROM_JOB', '
<!doctype html>
<html xmlns=http://www.w3.org/1999/xhtml xmlns:o=urn:schemas-microsoft-com:office:office xmlns:v=urn:schemas-microsoft-com:vml>
<head>
    <!--[if gte mso 9]><xml><o:OfficeDocumentSettings><o:AllowPNG/><o:PixelsPerInch>96</o:PixelsPerInch></o:OfficeDocumentSettings></xml><![endif]-->
    <meta content="text/html; charset=utf-8" http-equiv=Content-Type>
    <meta content="width=device-width" name=viewport>
    <!--[if !mso]><!-->
    <meta content="IE=edge" http-equiv=X-UA-Compatible>
    <!--<![endif]-->
    <title></title>
    <!--[if !mso]><!-->
    <link href="https://fonts.googleapis.com/css?family=Open+Sans" rel=stylesheet>
    <link href="https://fonts.googleapis.com/css?family=Lato" rel=stylesheet>
    <link href="https://fonts.googleapis.com/css?family=Nunito" rel=stylesheet>
    <!--<![endif]-->
    <style>body{margin:0;padding:0}table,td,tr{vertical-align:top;border-collapse:collapse}*{line-height:inherit}a[x-apple-data-detectors=true]{color:inherit!important;text-decoration:none!important}</style>
    <style id=media-query>@media (max-width:500px){.block-grid,.col{min-width:320px!important;max-width:100%!important;display:block!important}.block-grid{width:100%!important}.col{width:100%!important}.col>div{margin:0 auto}img.fullwidth,img.fullwidthOnMobile{max-width:100%!important}.no-stack .col{min-width:0!important;display:table-cell!important}.no-stack.two-up .col{width:50%!important}.no-stack .col.num4{width:33%!important}.no-stack .col.num8{width:66%!important}.no-stack .col.num4{width:33%!important}.no-stack .col.num3{width:25%!important}.no-stack .col.num6{width:50%!important}.no-stack .col.num9{width:75%!important}.video-block{max-width:none!important}.mobile_hide{min-height:0;max-height:0;max-width:0;display:none;overflow:hidden;font-size:0}.desktop_hide{display:block!important;max-height:none!important}}</style>
</head>
<body class=clean-body style=margin:0;padding:0;-webkit-text-size-adjust:100%;background-color:#fff>
<!--[if IE]><div class="ie-browser"><![endif]-->
<table bgcolor=#FFFFFF cellpadding=0 cellspacing=0 class=nl-container role=presentation style="table-layout:fixed;vertical-align:top;min-width:320px;Margin:0 auto;border-spacing:0;border-collapse:collapse;mso-table-lspace:0;mso-table-rspace:0;background-color:#fff;width:100%" valign=top width=100%>
    <tbody>
    <tr style=vertical-align:top valign=top>
        <td style=word-break:break-word;vertical-align:top valign=top>
            <!--[if (mso)|(IE)]><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td align="center" style="background-color:#FFFFFF"><![endif]-->
            <div style=background-color:#f4f4f4>
                <div class=block-grid style="Margin:0 auto;min-width:320px;max-width:480px;overflow-wrap:break-word;word-wrap:break-word;word-break:break-word;background-color:transparent">
                    <div style=border-collapse:collapse;display:table;width:100%;background-color:transparent>
                        <!--[if (mso)|(IE)]><table width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f4f4f4;"><tr><td align="center"><table cellpadding="0" cellspacing="0" border="0" style="width:480px"><tr class="layout-full-width" style="background-color:transparent"><![endif]-->
                        <!--[if (mso)|(IE)]><td align="center" width="480" style="background-color:transparent;width:480px; border-top: 0px solid transparent; border-left: 0px solid transparent; border-bottom: 0px solid transparent; border-right: 0px solid transparent;" valign="top"><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 0px; padding-left: 0px; padding-top:5px; padding-bottom:5px;"><![endif]-->
                        <div class="col num12" style=min-width:320px;max-width:480px;display:table-cell;vertical-align:top;width:480px>
                            <div style=width:100%!important>
                                <!--[if (!mso)&(!IE)]><!-->
                                <div style="border-top:0 solid transparent;border-left:0 solid transparent;border-bottom:0 solid transparent;border-right:0 solid transparent;padding-top:5px;padding-bottom:5px;padding-right:0;padding-left:0">
                                    <!--<![endif]-->
                                    <div></div>
                                    <!--[if (!mso)&(!IE)]><!-->
                                </div>
                                <!--<![endif]-->
                            </div>
                        </div>
                        <!--[if (mso)|(IE)]></td></tr></table><![endif]-->
                        <!--[if (mso)|(IE)]></td></tr></table></td></tr></table><![endif]-->
                    </div>
                </div>
            </div>
            <div style=background-color:#f4f4f4>
                <div class=block-grid style="Margin:0 auto;min-width:320px;max-width:480px;overflow-wrap:break-word;word-wrap:break-word;word-break:break-word;background-color:#fff">
                    <div style=border-collapse:collapse;display:table;width:100%;background-color:#fff>
                        <!--[if (mso)|(IE)]><table width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f4f4f4;"><tr><td align="center"><table cellpadding="0" cellspacing="0" border="0" style="width:480px"><tr class="layout-full-width" style="background-color:#ffffff"><![endif]-->
                        <!--[if (mso)|(IE)]><td align="center" width="480" style="background-color:#ffffff;width:480px; border-top: 0px solid transparent; border-left: 0px solid transparent; border-bottom: 0px solid transparent; border-right: 0px solid transparent;" valign="top"><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 0px; padding-left: 0px; padding-top:5px; padding-bottom:5px;"><![endif]-->
                        <div class="col num12" style=min-width:320px;max-width:480px;display:table-cell;vertical-align:top;width:480px>
                            <div style=width:100%!important>
                                <!--[if (!mso)&(!IE)]><!-->
                                <div style="border-top:0 solid transparent;border-left:0 solid transparent;border-bottom:0 solid transparent;border-right:0 solid transparent;padding-top:5px;padding-bottom:5px;padding-right:0;padding-left:0">
                                    <!--<![endif]-->
                                    <div align=center class="img-container center fixedwidth" style=padding-right:0;padding-left:0>
                                        <!--[if mso]><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr style="line-height:0px"><td style="padding-right: 0px;padding-left: 0px;" align="center"><![endif]-->
                                        <div style=font-size:1px;line-height:32px> </div><img align=center border=0 class="center fixedwidth" src=cid:streem-color style=text-decoration:none;-ms-interpolation-mode:bicubic;height:auto;border:0;width:100%;max-width:96px;display:block width=96>
                                        <div style=font-size:1px;line-height:32px> </div>
                                        <!--[if mso]></td></tr></table><![endif]-->
                                    </div>
                                    <!--[if mso]><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 42px; padding-left: 42px; padding-top: 0px; padding-bottom: 32px; font-family: Arial, sans-serif"><![endif]-->
                                    <div style="color:#555;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;line-height:1.2;padding-top:0;padding-right:42px;padding-bottom:32px;padding-left:42px">
                                        <div style="line-height:1.2;font-size:12px;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;color:#555;mso-line-height-alt:14px">
                                            <p style="font-size:20px;line-height:1.2;text-align:center;word-break:break-word;font-family:Nunito,Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:24px;margin:0"><span style=font-size:20px;color:#000>You were unassigned from a Job you were assigned to earlier</span></p>
                                        </div>
                                    </div>
                                    <!--[if mso]></td></tr></table><![endif]-->
                                    <table border=0 cellpadding=0 cellspacing=0 class=divider role=presentation style=table-layout:fixed;vertical-align:top;border-spacing:0;border-collapse:collapse;mso-table-lspace:0;mso-table-rspace:0;min-width:100%;-ms-text-size-adjust:100%;-webkit-text-size-adjust:100% valign=top width=100%>
                                        <tbody>
                                        <tr style=vertical-align:top valign=top>
                                            <td class=divider_inner style=word-break:break-word;vertical-align:top;min-width:100%;-ms-text-size-adjust:100%;-webkit-text-size-adjust:100%;padding-top:0;padding-right:0;padding-bottom:32px;padding-left:0 valign=top>
                                                <table align=center border=0 cellpadding=0 cellspacing=0 class=divider_content role=presentation style="table-layout:fixed;vertical-align:top;border-spacing:0;border-collapse:collapse;mso-table-lspace:0;mso-table-rspace:0;border-top:1px solid #eee;width:100%" valign=top width=100%>
                                                    <tbody>
                                                    <tr style=vertical-align:top valign=top>
                                                        <td style=word-break:break-word;vertical-align:top;-ms-text-size-adjust:100%;-webkit-text-size-adjust:100% valign=top><span></span></td>
                                                    </tr>
                                                    </tbody>
                                                </table>
                                            </td>
                                        </tr>
                                        </tbody>
                                    </table>
                                    <!--[if mso]><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 32px; padding-left: 32px; padding-top: 0px; padding-bottom: 24px; font-family: Arial, sans-serif"><![endif]-->
                                    <div style="color:#555;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;line-height:1.5;padding-top:0;padding-right:32px;padding-bottom:24px;padding-left:32px">
                                        <div style="line-height:1.5;font-size:12px;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;color:#555;mso-line-height-alt:18px">
                                            <p style="font-size:14px;line-height:1.5;word-break:break-word;text-align:left;font-family:Nunito,Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:21px;margin:0"><span style=font-size:14px;color:#000>Hello, </span></p>
                                            <p style="font-size:14px;line-height:1.5;word-break:break-word;text-align:left;font-family:Nunito,Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:21px;margin:0"> </p>
                                            <p style="font-size:14px;line-height:1.5;word-break:break-word;text-align:left;font-family:Nunito,Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:21px;margin:0"><span style=font-size:14px;color:#000>You were unassigned from a Job that you were assigned to earlier. </span><span style=font-size:14px;color:#000>Contact your Supervisor to know more about this. </span></p>
                                        </div>
                                    </div>
                                    <!--[if mso]></td></tr></table><![endif]-->
                                    <!--[if mso]><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 32px; padding-left: 32px; padding-top: 0px; padding-bottom: 60px; font-family: Arial, sans-serif"><![endif]-->
                                    <div style="color:#393d47;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;line-height:1.5;padding-top:0;padding-right:32px;padding-bottom:60px;padding-left:32px">
                                        <div style="line-height:1.5;font-size:12px;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;color:#393d47;mso-line-height-alt:18px">
                                            <p style="font-size:14px;line-height:1.5;word-break:break-word;font-family:Nunito,Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:21px;margin:0"><span style=color:#000>Warm Regards</span></p>
                                            <p style="font-size:14px;line-height:1.5;word-break:break-word;font-family:Nunito,Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:21px;margin:0"><span style=color:#000>CLEEN App</span></p>
                                        </div>
                                    </div>
                                    <!--[if mso]></td></tr></table><![endif]-->
                                    <!--[if (!mso)&(!IE)]><!-->
                                </div>
                                <!--<![endif]-->
                            </div>
                        </div>
                        <!--[if (mso)|(IE)]></td></tr></table><![endif]-->
                        <!--[if (mso)|(IE)]></td></tr></table></td></tr></table><![endif]-->
                    </div>
                </div>
            </div>
            <div style=background-color:#f4f4f4>
                <div class=block-grid style="Margin:0 auto;min-width:320px;max-width:480px;overflow-wrap:break-word;word-wrap:break-word;word-break:break-word;background-color:transparent">
                    <div style=border-collapse:collapse;display:table;width:100%;background-color:transparent>
                        <!--[if (mso)|(IE)]><table width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f4f4f4;"><tr><td align="center"><table cellpadding="0" cellspacing="0" border="0" style="width:480px"><tr class="layout-full-width" style="background-color:transparent"><![endif]-->
                        <!--[if (mso)|(IE)]><td align="center" width="480" style="background-color:transparent;width:480px; border-top: 0px solid transparent; border-left: 0px solid transparent; border-bottom: 0px solid transparent; border-right: 0px solid transparent;" valign="top"><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 0px; padding-left: 0px; padding-top:5px; padding-bottom:5px;"><![endif]-->
                        <div class="col num12" style=min-width:320px;max-width:480px;display:table-cell;vertical-align:top;width:480px>
                            <div style=width:100%!important>
                                <!--[if (!mso)&(!IE)]><!-->
                                <div style="border-top:0 solid transparent;border-left:0 solid transparent;border-bottom:0 solid transparent;border-right:0 solid transparent;padding-top:5px;padding-bottom:5px;padding-right:0;padding-left:0">
                                    <!--<![endif]-->
                                    <div class=mobile_hide>
                                        <!--[if mso]><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 10px; padding-left: 10px; padding-top: 24px; padding-bottom: 0px; font-family: Arial, sans-serif"><![endif]-->
                                        <div style="color:#393d47;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;line-height:1.2;padding-top:24px;padding-right:10px;padding-bottom:0;padding-left:10px">
                                            <div style="line-height:1.2;font-size:12px;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;color:#393d47;mso-line-height-alt:14px">
                                                <p style="font-size:12px;line-height:1.2;word-break:break-word;text-align:center;font-family:Nunito,Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:14px;margin:0"><span style=font-size:12px;color:#000>To login to the CLEEN App open this link in a web browser (e.g. Google Chrome) - <span style=color:#1d84ff>${streemlogin}</span></span></p>
                                            </div>
                                        </div>
                                        <!--[if mso]></td></tr></table><![endif]-->
                                    </div>
                                    <!--[if (!mso)&(!IE)]><!-->
                                </div>
                                <!--<![endif]-->
                            </div>
                        </div>
                        <!--[if (mso)|(IE)]></td></tr></table><![endif]-->
                        <!--[if (mso)|(IE)]></td></tr></table></td></tr></table><![endif]-->
                    </div>
                </div>
            </div>
            <!--[if (mso)|(IE)]></td></tr></table><![endif]-->
        </td>
    </tr>
    </tbody>
</table>
<!--[if (IE)]></div><![endif]-->
</body>
</html>');

--changeset sathyam:02-seed-data-5
INSERT INTO email_templates(id, name, content)
VALUES (2, 'USER_ASSIGNED_TO_JOB', '
<!doctype html>
<html xmlns=http://www.w3.org/1999/xhtml xmlns:o=urn:schemas-microsoft-com:office:office xmlns:v=urn:schemas-microsoft-com:vml xmlns:th=http://www.w3.org/1999/xhtml>
<head>
    <!--[if gte mso 9]><xml><o:OfficeDocumentSettings><o:AllowPNG/><o:PixelsPerInch>96</o:PixelsPerInch></o:OfficeDocumentSettings></xml><![endif]-->
    <meta content="text/html; charset=utf-8" http-equiv=Content-Type>
    <meta content="width=device-width" name=viewport>
    <!--[if !mso]><!-->
    <meta content="IE=edge" http-equiv=X-UA-Compatible>
    <!--<![endif]-->
    <title></title>
    <!--[if !mso]><!-->
    <link href="https://fonts.googleapis.com/css?family=Nunito" rel=stylesheet>
    <!--<![endif]-->
    <style>body{margin:0;padding:0}table,td,tr{vertical-align:top;border-collapse:collapse}*{line-height:inherit}a[x-apple-data-detectors=true]{color:inherit!important;text-decoration:none!important}</style>
    <style id=media-query>@media (max-width:500px){.block-grid,.col{min-width:320px!important;max-width:100%!important;display:block!important}.block-grid{width:100%!important}.col{width:100%!important}.col_cont{margin:0 auto}img.fullwidth,img.fullwidthOnMobile{max-width:100%!important}.no-stack .col{min-width:0!important;display:table-cell!important}.no-stack.two-up .col{width:50%!important}.no-stack .col.num2{width:16.6%!important}.no-stack .col.num3{width:25%!important}.no-stack .col.num4{width:33%!important}.no-stack .col.num5{width:41.6%!important}.no-stack .col.num6{width:50%!important}.no-stack .col.num7{width:58.3%!important}.no-stack .col.num8{width:66.6%!important}.no-stack .col.num9{width:75%!important}.no-stack .col.num10{width:83.3%!important}.video-block{max-width:none!important}.mobile_hide{min-height:0;max-height:0;max-width:0;display:none;overflow:hidden;font-size:0}.desktop_hide{display:block!important;max-height:none!important}}</style>
</head>
<body class=clean-body style=margin:0;padding:0;-webkit-text-size-adjust:100%;background-color:#f4f4f4>
<!--[if IE]><div class="ie-browser"><![endif]-->
<table bgcolor=#f4f4f4 cellpadding=0 cellspacing=0 class=nl-container role=presentation style=table-layout:fixed;vertical-align:top;min-width:320px;border-spacing:0;border-collapse:collapse;mso-table-lspace:0;mso-table-rspace:0;background-color:#f4f4f4;width:100% valign=top width=100%>
    <tbody>
    <tr style=vertical-align:top valign=top>
        <td style=word-break:break-word;vertical-align:top valign=top>
            <!--[if (mso)|(IE)]><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td align="center" style="background-color:#f4f4f4"><![endif]-->
            <div style=background-color:#f4f4f4>
                <div class=block-grid style="min-width:320px;max-width:480px;overflow-wrap:break-word;word-wrap:break-word;word-break:break-word;Margin:0 auto;background-color:#f4f4f4">
                    <div style=border-collapse:collapse;display:table;width:100%;background-color:#f4f4f4>
                        <!--[if (mso)|(IE)]><table width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f4f4f4;"><tr><td align="center"><table cellpadding="0" cellspacing="0" border="0" style="width:480px"><tr class="layout-full-width" style="background-color:#f4f4f4"><![endif]-->
                        <!--[if (mso)|(IE)]><td align="center" width="480" style="background-color:#f4f4f4;width:480px; border-top: 0px solid transparent; border-left: 0px solid transparent; border-bottom: 0px solid transparent; border-right: 0px solid transparent;" valign="top"><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 0px; padding-left: 0px; padding-top:50px; padding-bottom:0px;"><![endif]-->
                        <div class="col num12" style=min-width:320px;max-width:480px;display:table-cell;vertical-align:top;width:480px>
                            <div class=col_cont style=width:100%!important>
                                <!--[if (!mso)&(!IE)]><!-->
                                <div style="border-top:0 solid transparent;border-left:0 solid transparent;border-bottom:0 solid transparent;border-right:0 solid transparent;padding-top:50px;padding-bottom:0;padding-right:0;padding-left:0">
                                    <!--<![endif]-->
                                    <div></div>
                                    <!--[if (!mso)&(!IE)]><!-->
                                </div>
                                <!--<![endif]-->
                            </div>
                        </div>
                        <!--[if (mso)|(IE)]></td></tr></table><![endif]-->
                        <!--[if (mso)|(IE)]></td></tr></table></td></tr></table><![endif]-->
                    </div>
                </div>
            </div>
            <div style=background-color:#f4f4f4>
                <div class=block-grid style="min-width:320px;max-width:480px;overflow-wrap:break-word;word-wrap:break-word;word-break:break-word;Margin:0 auto;background-color:#fff">
                    <div style=border-collapse:collapse;display:table;width:100%;background-color:#fff>
                        <!--[if (mso)|(IE)]><table width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f4f4f4;"><tr><td align="center"><table cellpadding="0" cellspacing="0" border="0" style="width:480px"><tr class="layout-full-width" style="background-color:#ffffff"><![endif]-->
                        <!--[if (mso)|(IE)]><td align="center" width="480" style="background-color:#ffffff;width:480px; border-top: 0px solid transparent; border-left: 0px solid transparent; border-bottom: 0px solid transparent; border-right: 0px solid transparent;" valign="top"><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 0px; padding-left: 0px; padding-top:32px; padding-bottom:32px;"><![endif]-->
                        <div class="col num12" style=min-width:320px;max-width:480px;display:table-cell;vertical-align:top;width:480px>
                            <div class=col_cont style=width:100%!important>
                                <!--[if (!mso)&(!IE)]><!-->
                                <div style="border-top:0 solid transparent;border-left:0 solid transparent;border-bottom:0 solid transparent;border-right:0 solid transparent;padding-top:32px;padding-bottom:32px;padding-right:0;padding-left:0">
                                    <!--<![endif]-->
                                    <div align=center class="img-container center fixedwidth" style=padding-right:0;padding-left:0>
                                        <!--[if mso]><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr style="line-height:0px"><td style="padding-right: 0px;padding-left: 0px;" align="center"><![endif]--><img align=center alt="Alternate text" border=0 class="center fixedwidth" src=cid:streem-color style=text-decoration:none;-ms-interpolation-mode:bicubic;height:auto;border:0;width:100%;max-width:144px;display:block title="Alternate text" width=144>
                                        <!--[if mso]></td></tr></table><![endif]-->
                                    </div>
                                    <!--[if (!mso)&(!IE)]><!-->
                                </div>
                                <!--<![endif]-->
                            </div>
                        </div>
                        <!--[if (mso)|(IE)]></td></tr></table><![endif]-->
                        <!--[if (mso)|(IE)]></td></tr></table></td></tr></table><![endif]-->
                    </div>
                </div>
            </div>
            <div style=background-color:#f4f4f4>
                <div class=block-grid style="min-width:320px;max-width:480px;overflow-wrap:break-word;word-wrap:break-word;word-break:break-word;Margin:0 auto;background-color:#fff">
                    <div style=border-collapse:collapse;display:table;width:100%;background-color:#fff>
                        <!--[if (mso)|(IE)]><table width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f4f4f4;"><tr><td align="center"><table cellpadding="0" cellspacing="0" border="0" style="width:480px"><tr class="layout-full-width" style="background-color:#ffffff"><![endif]-->
                        <!--[if (mso)|(IE)]><td align="center" width="480" style="background-color:#ffffff;width:480px; border-top: 0px solid transparent; border-left: 0px solid transparent; border-bottom: 0px solid transparent; border-right: 0px solid transparent;" valign="top"><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 0px; padding-left: 0px; padding-top:5px; padding-bottom:5px;"><![endif]-->
                        <div class="col num12" style=min-width:320px;max-width:480px;display:table-cell;vertical-align:top;width:480px>
                            <div class=col_cont style=width:100%!important>
                                <!--[if (!mso)&(!IE)]><!-->
                                <div style="border-top:0 solid transparent;border-left:0 solid transparent;border-bottom:0 solid transparent;border-right:0 solid transparent;padding-top:5px;padding-bottom:5px;padding-right:0;padding-left:0">
                                    <!--<![endif]-->
                                    <!--[if mso]><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 40px; padding-left: 40px; padding-top: 0px; padding-bottom: 32px; font-family: Arial, sans-serif"><![endif]-->
                                    <div style="color:#000;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;line-height:1.2;padding-top:0;padding-right:40px;padding-bottom:32px;padding-left:40px">
                                        <div style="line-height:1.2;font-size:12px;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;color:#000;mso-line-height-alt:14px">
                                            <p style="font-size:20px;line-height:1.2;word-break:break-word;text-align:center;font-family:Nunito,Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:24px;margin:0"><span style=font-size:20px>A New Job is Assigned to You</span></p>
                                        </div>
                                    </div>
                                    <!--[if mso]></td></tr></table><![endif]-->
                                    <table border=0 cellpadding=0 cellspacing=0 class=divider role=presentation style=table-layout:fixed;vertical-align:top;border-spacing:0;border-collapse:collapse;mso-table-lspace:0;mso-table-rspace:0;min-width:100%;-ms-text-size-adjust:100%;-webkit-text-size-adjust:100% valign=top width=100%>
                                        <tbody>
                                        <tr style=vertical-align:top valign=top>
                                            <td class=divider_inner style=word-break:break-word;vertical-align:top;min-width:100%;-ms-text-size-adjust:100%;-webkit-text-size-adjust:100%;padding-top:0;padding-right:10px;padding-bottom:0;padding-left:10px valign=top>
                                                <table align=center border=0 cellpadding=0 cellspacing=0 class=divider_content role=presentation style="table-layout:fixed;vertical-align:top;border-spacing:0;border-collapse:collapse;mso-table-lspace:0;mso-table-rspace:0;border-top:1px solid #eee;width:100%" valign=top width=100%>
                                                    <tbody>
                                                    <tr style=vertical-align:top valign=top>
                                                        <td style=word-break:break-word;vertical-align:top;-ms-text-size-adjust:100%;-webkit-text-size-adjust:100% valign=top><span></span></td>
                                                    </tr>
                                                    </tbody>
                                                </table>
                                            </td>
                                        </tr>
                                        </tbody>
                                    </table>
                                    <!--[if (!mso)&(!IE)]><!-->
                                </div>
                                <!--<![endif]-->
                            </div>
                        </div>
                        <!--[if (mso)|(IE)]></td></tr></table><![endif]-->
                        <!--[if (mso)|(IE)]></td></tr></table></td></tr></table><![endif]-->
                    </div>
                </div>
            </div>
            <div style=background-color:transparent>
                <div class=block-grid style="min-width:320px;max-width:480px;overflow-wrap:break-word;word-wrap:break-word;word-break:break-word;Margin:0 auto;background-color:#fff">
                    <div style=border-collapse:collapse;display:table;width:100%;background-color:#fff>
                        <!--[if (mso)|(IE)]><table width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:transparent;"><tr><td align="center"><table cellpadding="0" cellspacing="0" border="0" style="width:480px"><tr class="layout-full-width" style="background-color:#ffffff"><![endif]-->
                        <!--[if (mso)|(IE)]><td align="center" width="480" style="background-color:#ffffff;width:480px; border-top: 0px solid transparent; border-left: 0px solid transparent; border-bottom: 0px solid transparent; border-right: 0px solid transparent;" valign="top"><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 0px; padding-left: 0px; padding-top:5px; padding-bottom:5px;"><![endif]-->
                        <div class="col num12" style=min-width:320px;max-width:480px;display:table-cell;vertical-align:top;width:480px>
                            <div class=col_cont style=width:100%!important>
                                <!--[if (!mso)&(!IE)]><!-->
                                <div style="border-top:0 solid transparent;border-left:0 solid transparent;border-bottom:0 solid transparent;border-right:0 solid transparent;padding-top:5px;padding-bottom:5px;padding-right:0;padding-left:0">
                                    <!--<![endif]-->
                                    <!--[if mso]><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 32px; padding-left: 32px; padding-top: 32px; padding-bottom: 24px; font-family: Arial, sans-serif"><![endif]-->
                                    <div style="color:#000;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;line-height:1.5;padding-top:32px;padding-right:32px;padding-bottom:24px;padding-left:32px">
                                        <div style="line-height:1.5;font-size:12px;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;color:#000;mso-line-height-alt:18px">
                                            <p style="font-size:14px;line-height:1.5;word-break:break-word;font-family:Nunito,Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:21px;margin:0">Hello,</p>
                                            <p style="font-size:14px;line-height:1.5;word-break:break-word;font-family:Nunito,Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:21px;margin:0"> </p>
                                            <p style="font-size:14px;line-height:1.5;word-break:break-word;font-family:Nunito,Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:21px;margin:0">A new Job has been assigned to you. Check it out by clicking the button below.</p>
                                        </div>
                                    </div>
                                    <!--[if mso]></td></tr></table><![endif]-->
                                    <!--[if (!mso)&(!IE)]><!-->
                                </div>
                                <!--<![endif]-->
                            </div>
                        </div>
                        <!--[if (mso)|(IE)]></td></tr></table><![endif]-->
                        <!--[if (mso)|(IE)]></td></tr></table></td></tr></table><![endif]-->
                    </div>
                </div>
            </div>
            <div style=background-color:transparent>
                <div class=block-grid style="min-width:320px;max-width:480px;overflow-wrap:break-word;word-wrap:break-word;word-break:break-word;Margin:0 auto;background-color:#fff">
                    <div style=border-collapse:collapse;display:table;width:100%;background-color:#fff>
                        <!--[if (mso)|(IE)]><table width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:transparent;"><tr><td align="center"><table cellpadding="0" cellspacing="0" border="0" style="width:480px"><tr class="layout-full-width" style="background-color:#ffffff"><![endif]-->
                        <!--[if (mso)|(IE)]><td align="center" width="480" style="background-color:#ffffff;width:480px; border-top: 0px solid transparent; border-left: 0px solid transparent; border-bottom: 0px solid transparent; border-right: 0px solid transparent;" valign="top"><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 0px; padding-left: 0px; padding-top:5px; padding-bottom:5px;"><![endif]-->
                        <div class="col num12" style=min-width:320px;max-width:480px;display:table-cell;vertical-align:top;width:480px>
                            <div class=col_cont style=width:100%!important>
                                <!--[if (!mso)&(!IE)]><!-->
                                <div style="border-top:0 solid transparent;border-left:0 solid transparent;border-bottom:0 solid transparent;border-right:0 solid transparent;padding-top:5px;padding-bottom:5px;padding-right:0;padding-left:0">
                                    <!--<![endif]-->
                                    <a href=${job} style=text-decoration:none;cursor:pointer>
                                        <div align=left class=button-container style=padding-top:0;padding-right:32px;padding-bottom:24px;padding-left:32px>
                                            <!--[if mso]><table width="100%" cellpadding="0" cellspacing="0" border="0" style="border-spacing: 0; border-collapse: collapse; mso-table-lspace:0pt; mso-table-rspace:0pt;"><tr><td style="padding-top: 0px; padding-right: 32px; padding-bottom: 24px; padding-left: 32px" align="left"><v:roundrect xmlns:v="urn:schemas-microsoft-com:vml" xmlns:w="urn:schemas-microsoft-com:office:word" href="" style="height:39pt; width:129.75pt; v-text-anchor:middle;" arcsize="8%" stroke="false" fillcolor="#1d84ff"><w:anchorlock/><v:textbox inset="0,0,0,0"><center style="color:#ffffff; font-family:Arial, sans-serif; font-size:14px"><![endif]-->
                                            <div style="text-decoration:none;display:inline-block;color:#fff;background-color:#1d84ff;border-radius:4px;-webkit-border-radius:4px;-moz-border-radius:4px;width:auto;width:auto;border-top:0 dotted #fff;border-right:0 dotted #fff;border-bottom:0 dotted #fff;border-left:0 dotted #fff;padding-top:10px;padding-bottom:10px;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;text-align:center;mso-border-alt:none;word-break:keep-all"><span style=padding-left:20px;padding-right:20px;font-size:14px;display:inline-block><span style="font-size:16px;line-height:2;word-break:break-word;font-family:Nunito,Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:32px"><span data-mce-style="font-size: 14px; line-height: 28px;" style=font-size:14px;line-height:28px>View Assigned Job</span></span></span></div>
                                            <!--[if mso]></center></v:textbox></v:roundrect></td></tr></table><![endif]-->
                                        </div>
                                    </a>
                                    <!--[if (!mso)&(!IE)]><!-->
                                </div>
                                <!--<![endif]-->
                            </div>
                        </div>
                        <!--[if (mso)|(IE)]></td></tr></table><![endif]-->
                        <!--[if (mso)|(IE)]></td></tr></table></td></tr></table><![endif]-->
                    </div>
                </div>
            </div>
            <div style=background-color:transparent>
                <div class=block-grid style="min-width:320px;max-width:480px;overflow-wrap:break-word;word-wrap:break-word;word-break:break-word;Margin:0 auto;background-color:#fff">
                    <div style=border-collapse:collapse;display:table;width:100%;background-color:#fff>
                        <!--[if (mso)|(IE)]><table width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:transparent;"><tr><td align="center"><table cellpadding="0" cellspacing="0" border="0" style="width:480px"><tr class="layout-full-width" style="background-color:#ffffff"><![endif]-->
                        <!--[if (mso)|(IE)]><td align="center" width="480" style="background-color:#ffffff;width:480px; border-top: 0px solid transparent; border-left: 0px solid transparent; border-bottom: 0px solid transparent; border-right: 0px solid transparent;" valign="top"><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 0px; padding-left: 0px; padding-top:5px; padding-bottom:5px;"><![endif]-->
                        <div class="col num12" style=min-width:320px;max-width:480px;display:table-cell;vertical-align:top;width:480px>
                            <div class=col_cont style=width:100%!important>
                                <!--[if (!mso)&(!IE)]><!-->
                                <div style="border-top:0 solid transparent;border-left:0 solid transparent;border-bottom:0 solid transparent;border-right:0 solid transparent;padding-top:5px;padding-bottom:5px;padding-right:0;padding-left:0">
                                    <!--<![endif]-->
                                    <!--[if mso]><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 32px; padding-left: 32px; padding-top: 00px; padding-bottom: 32px; font-family: Arial, sans-serif"><![endif]-->
                                    <div style="color:#000;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;line-height:1.5;padding-top:00px;padding-right:32px;padding-bottom:32px;padding-left:32px">
                                        <div style="line-height:1.5;font-size:12px;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;color:#000;mso-line-height-alt:18px">
                                            <p style="font-size:12px;line-height:1.5;word-break:break-word;text-align:left;font-family:Nunito,Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:18px;margin:0"><span style=font-size:12px>Click the above button, or copy and paste the following link on your web browser: </span><span style=font-size:12px>${job}</span></p>
                                        </div>
                                    </div>
                                    <!--[if mso]></td></tr></table><![endif]-->
                                    <!--[if (!mso)&(!IE)]><!-->
                                </div>
                                <!--<![endif]-->
                            </div>
                        </div>
                        <!--[if (mso)|(IE)]></td></tr></table><![endif]-->
                        <!--[if (mso)|(IE)]></td></tr></table></td></tr></table><![endif]-->
                    </div>
                </div>
            </div>
            <div style=background-color:transparent>
                <div class=block-grid style="min-width:320px;max-width:480px;overflow-wrap:break-word;word-wrap:break-word;word-break:break-word;Margin:0 auto;background-color:#fff">
                    <div style=border-collapse:collapse;display:table;width:100%;background-color:#fff>
                        <!--[if (mso)|(IE)]><table width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:transparent;"><tr><td align="center"><table cellpadding="0" cellspacing="0" border="0" style="width:480px"><tr class="layout-full-width" style="background-color:#ffffff"><![endif]-->
                        <!--[if (mso)|(IE)]><td align="center" width="480" style="background-color:#ffffff;width:480px; border-top: 0px solid transparent; border-left: 0px solid transparent; border-bottom: 0px solid transparent; border-right: 0px solid transparent;" valign="top"><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 0px; padding-left: 0px; padding-top:5px; padding-bottom:5px;"><![endif]-->
                        <div class="col num12" style=min-width:320px;max-width:480px;display:table-cell;vertical-align:top;width:480px>
                            <div class=col_cont style=width:100%!important>
                                <!--[if (!mso)&(!IE)]><!-->
                                <div style="border-top:0 solid transparent;border-left:0 solid transparent;border-bottom:0 solid transparent;border-right:0 solid transparent;padding-top:5px;padding-bottom:5px;padding-right:0;padding-left:0">
                                    <!--<![endif]-->
                                    <!--[if mso]><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 32px; padding-left: 32px; padding-top: 20px; padding-bottom: 60px; font-family: Arial, sans-serif"><![endif]-->
                                    <div style="color:#000;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;line-height:1.2;padding-top:20px;padding-right:32px;padding-bottom:60px;padding-left:32px">
                                        <div style="line-height:1.2;font-size:12px;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;color:#000;mso-line-height-alt:14px">
                                            <p style="font-size:14px;line-height:1.2;word-break:break-word;font-family:Nunito,Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:17px;margin:0"><span style=font-size:14px>Warm Regards,</span></p>
                                            <p style="font-size:14px;line-height:1.2;word-break:break-word;font-family:Nunito,Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:17px;margin:0"><span style=font-size:14px>CLEEN</span></p>
                                        </div>
                                    </div>
                                    <!--[if mso]></td></tr></table><![endif]-->
                                    <!--[if (!mso)&(!IE)]><!-->
                                </div>
                                <!--<![endif]-->
                            </div>
                        </div>
                        <!--[if (mso)|(IE)]></td></tr></table><![endif]-->
                        <!--[if (mso)|(IE)]></td></tr></table></td></tr></table><![endif]-->
                    </div>
                </div>
            </div>
            <div style=background-color:#f4f4f4>
                <div class=block-grid style="min-width:320px;max-width:480px;overflow-wrap:break-word;word-wrap:break-word;word-break:break-word;Margin:0 auto;background-color:#f4f4f4">
                    <div style=border-collapse:collapse;display:table;width:100%;background-color:#f4f4f4>
                        <!--[if (mso)|(IE)]><table width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f4f4f4;"><tr><td align="center"><table cellpadding="0" cellspacing="0" border="0" style="width:480px"><tr class="layout-full-width" style="background-color:#f4f4f4"><![endif]-->
                        <!--[if (mso)|(IE)]><td align="center" width="480" style="background-color:#f4f4f4;width:480px; border-top: 0px solid transparent; border-left: 0px solid transparent; border-bottom: 0px solid transparent; border-right: 0px solid transparent;" valign="top"><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 0px; padding-left: 0px; padding-top:5px; padding-bottom:5px;"><![endif]-->
                        <div class="col num12" style=min-width:320px;max-width:480px;display:table-cell;vertical-align:top;width:480px>
                            <div class=col_cont style=width:100%!important>
                                <!--[if (!mso)&(!IE)]><!-->
                                <div style="border-top:0 solid transparent;border-left:0 solid transparent;border-bottom:0 solid transparent;border-right:0 solid transparent;padding-top:5px;padding-bottom:5px;padding-right:0;padding-left:0">
                                    <!--<![endif]-->
                                    <!--[if mso]><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 32px; padding-left: 32px; padding-top: 20px; padding-bottom: 20px; font-family: Arial, sans-serif"><![endif]-->
                                    <div style="color:#000;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;line-height:1.2;padding-top:20px;padding-right:32px;padding-bottom:20px;padding-left:32px">
                                        <div style="line-height:1.2;font-size:12px;color:#000;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:14px">
                                            <p style=line-height:1.2;word-break:break-word;text-align:center;mso-line-height-alt:14px;margin:0><span style=color:#000>To log in to CLEEN, open this link in a web browser (e.g. Google Chrome) - <span style=color:#1d84ff>${streemlogin}</span></span></p>
                                        </div>
                                    </div>
                                    <!--[if mso]></td></tr></table><![endif]-->
                                    <!--[if (!mso)&(!IE)]><!-->
                                </div>
                                <!--<![endif]-->
                            </div>
                        </div>
                        <!--[if (mso)|(IE)]></td></tr></table><![endif]-->
                        <!--[if (mso)|(IE)]></td></tr></table></td></tr></table><![endif]-->
                    </div>
                </div>
            </div>
            <!--[if (mso)|(IE)]></td></tr></table><![endif]-->
        </td>
    </tr>
    </tbody>
</table>
<!--[if (IE)]></div><![endif]-->
</body>
</html>');

--changeset sathyam:02-seed-data-6
INSERT INTO email_templates(id, name, content)
VALUES (3, 'APPROVAl_REQUEST', '
<!doctype html>
<html xmlns=http://www.w3.org/1999/xhtml xmlns:o=urn:schemas-microsoft-com:office:office xmlns:v=urn:schemas-microsoft-com:vml>
<head>
    <!--[if gte mso 9]><xml><o:OfficeDocumentSettings><o:AllowPNG/><o:PixelsPerInch>96</o:PixelsPerInch></o:OfficeDocumentSettings></xml><![endif]-->
    <meta content="text/html; charset=utf-8" http-equiv=Content-Type>
    <meta content="width=device-width" name=viewport>
    <!--[if !mso]><!-->
    <meta content="IE=edge" http-equiv=X-UA-Compatible>
    <!--<![endif]-->
    <title></title>
    <!--[if !mso]><!-->
    <link href="https://fonts.googleapis.com/css?family=Nunito" rel=stylesheet>
    <!--<![endif]-->
    <style>body{margin:0;padding:0}table,td,tr{vertical-align:top;border-collapse:collapse}*{line-height:inherit}a[x-apple-data-detectors=true]{color:inherit!important;text-decoration:none!important}</style>
    <style id=media-query>@media (max-width:500px){.block-grid,.col{min-width:320px!important;max-width:100%!important;display:block!important}.block-grid{width:100%!important}.col{width:100%!important}.col_cont{margin:0 auto}img.fullwidth,img.fullwidthOnMobile{max-width:100%!important}.no-stack .col{min-width:0!important;display:table-cell!important}.no-stack.two-up .col{width:50%!important}.no-stack .col.num2{width:16.6%!important}.no-stack .col.num3{width:25%!important}.no-stack .col.num4{width:33%!important}.no-stack .col.num5{width:41.6%!important}.no-stack .col.num6{width:50%!important}.no-stack .col.num7{width:58.3%!important}.no-stack .col.num8{width:66.6%!important}.no-stack .col.num9{width:75%!important}.no-stack .col.num10{width:83.3%!important}.video-block{max-width:none!important}.mobile_hide{min-height:0;max-height:0;max-width:0;display:none;overflow:hidden;font-size:0}.desktop_hide{display:block!important;max-height:none!important}}</style>
</head>
<body class=clean-body style=margin:0;padding:0;-webkit-text-size-adjust:100%;background-color:#f4f4f4>
<!--[if IE]><div class="ie-browser"><![endif]-->
<table bgcolor=#f4f4f4 cellpadding=0 cellspacing=0 class=nl-container role=presentation style=table-layout:fixed;vertical-align:top;min-width:320px;border-spacing:0;border-collapse:collapse;mso-table-lspace:0;mso-table-rspace:0;background-color:#f4f4f4;width:100% valign=top width=100%>
    <tbody>
    <tr style=vertical-align:top valign=top>
        <td style=word-break:break-word;vertical-align:top valign=top>
            <!--[if (mso)|(IE)]><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td align="center" style="background-color:#f4f4f4"><![endif]-->
            <div style=background-color:#f4f4f4>
                <div class=block-grid style="min-width:320px;max-width:480px;overflow-wrap:break-word;word-wrap:break-word;word-break:break-word;Margin:0 auto;background-color:#f4f4f4">
                    <div style=border-collapse:collapse;display:table;width:100%;background-color:#f4f4f4>
                        <!--[if (mso)|(IE)]><table width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f4f4f4;"><tr><td align="center"><table cellpadding="0" cellspacing="0" border="0" style="width:480px"><tr class="layout-full-width" style="background-color:#f4f4f4"><![endif]-->
                        <!--[if (mso)|(IE)]><td align="center" width="480" style="background-color:#f4f4f4;width:480px; border-top: 0px solid transparent; border-left: 0px solid transparent; border-bottom: 0px solid transparent; border-right: 0px solid transparent;" valign="top"><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 0px; padding-left: 0px; padding-top:50px; padding-bottom:0px;"><![endif]-->
                        <div class="col num12" style=min-width:320px;max-width:480px;display:table-cell;vertical-align:top;width:480px>
                            <div class=col_cont style=width:100%!important>
                                <!--[if (!mso)&(!IE)]><!-->
                                <div style="border-top:0 solid transparent;border-left:0 solid transparent;border-bottom:0 solid transparent;border-right:0 solid transparent;padding-top:50px;padding-bottom:0;padding-right:0;padding-left:0">
                                    <!--<![endif]-->
                                    <div></div>
                                    <!--[if (!mso)&(!IE)]><!-->
                                </div>
                                <!--<![endif]-->
                            </div>
                        </div>
                        <!--[if (mso)|(IE)]></td></tr></table><![endif]-->
                        <!--[if (mso)|(IE)]></td></tr></table></td></tr></table><![endif]-->
                    </div>
                </div>
            </div>
            <div style=background-color:#f4f4f4>
                <div class=block-grid style="min-width:320px;max-width:480px;overflow-wrap:break-word;word-wrap:break-word;word-break:break-word;Margin:0 auto;background-color:#fff">
                    <div style=border-collapse:collapse;display:table;width:100%;background-color:#fff>
                        <!--[if (mso)|(IE)]><table width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f4f4f4;"><tr><td align="center"><table cellpadding="0" cellspacing="0" border="0" style="width:480px"><tr class="layout-full-width" style="background-color:#ffffff"><![endif]-->
                        <!--[if (mso)|(IE)]><td align="center" width="480" style="background-color:#ffffff;width:480px; border-top: 0px solid transparent; border-left: 0px solid transparent; border-bottom: 0px solid transparent; border-right: 0px solid transparent;" valign="top"><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 0px; padding-left: 0px; padding-top:32px; padding-bottom:32px;"><![endif]-->
                        <div class="col num12" style=min-width:320px;max-width:480px;display:table-cell;vertical-align:top;width:480px>
                            <div class=col_cont style=width:100%!important>
                                <!--[if (!mso)&(!IE)]><!-->
                                <div style="border-top:0 solid transparent;border-left:0 solid transparent;border-bottom:0 solid transparent;border-right:0 solid transparent;padding-top:32px;padding-bottom:32px;padding-right:0;padding-left:0">
                                    <!--<![endif]-->
                                    <div align=center class="img-container center fixedwidth" style=padding-right:0;padding-left:0>
                                        <!--[if mso]><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr style="line-height:0px"><td style="padding-right: 0px;padding-left: 0px;" align="center"><![endif]--><img align=center alt="Alternate text" border=0 class="center fixedwidth" src=cid:streem-color style=text-decoration:none;-ms-interpolation-mode:bicubic;height:auto;border:0;width:100%;max-width:144px;display:block title="Alternate text" width=144>
                                        <!--[if mso]></td></tr></table><![endif]-->
                                    </div>
                                    <!--[if (!mso)&(!IE)]><!-->
                                </div>
                                <!--<![endif]-->
                            </div>
                        </div>
                        <!--[if (mso)|(IE)]></td></tr></table><![endif]-->
                        <!--[if (mso)|(IE)]></td></tr></table></td></tr></table><![endif]-->
                    </div>
                </div>
            </div>
            <div style=background-color:#f4f4f4>
                <div class=block-grid style="min-width:320px;max-width:480px;overflow-wrap:break-word;word-wrap:break-word;word-break:break-word;Margin:0 auto;background-color:#fff">
                    <div style=border-collapse:collapse;display:table;width:100%;background-color:#fff>
                        <!--[if (mso)|(IE)]><table width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f4f4f4;"><tr><td align="center"><table cellpadding="0" cellspacing="0" border="0" style="width:480px"><tr class="layout-full-width" style="background-color:#ffffff"><![endif]-->
                        <!--[if (mso)|(IE)]><td align="center" width="480" style="background-color:#ffffff;width:480px; border-top: 0px solid transparent; border-left: 0px solid transparent; border-bottom: 0px solid transparent; border-right: 0px solid transparent;" valign="top"><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 0px; padding-left: 0px; padding-top:5px; padding-bottom:5px;"><![endif]-->
                        <div class="col num12" style=min-width:320px;max-width:480px;display:table-cell;vertical-align:top;width:480px>
                            <div class=col_cont style=width:100%!important>
                                <!--[if (!mso)&(!IE)]><!-->
                                <div style="border-top:0 solid transparent;border-left:0 solid transparent;border-bottom:0 solid transparent;border-right:0 solid transparent;padding-top:5px;padding-bottom:5px;padding-right:0;padding-left:0">
                                    <!--<![endif]-->
                                    <!--[if mso]><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 40px; padding-left: 40px; padding-top: 0px; padding-bottom: 32px; font-family: Arial, sans-serif"><![endif]-->
                                    <div style="color:#000;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;line-height:1.2;padding-top:0;padding-right:40px;padding-bottom:32px;padding-left:40px">
                                        <div style="line-height:1.2;font-size:12px;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;color:#000;mso-line-height-alt:14px">
                                            <p style="font-size:20px;line-height:1.2;word-break:break-word;text-align:center;font-family:Nunito,Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:24px;margin:0"><span style=font-size:20px>An Operator has requested you to approve a Task</span></p>
                                        </div>
                                    </div>
                                    <!--[if mso]></td></tr></table><![endif]-->
                                    <table border=0 cellpadding=0 cellspacing=0 class=divider role=presentation style=table-layout:fixed;vertical-align:top;border-spacing:0;border-collapse:collapse;mso-table-lspace:0;mso-table-rspace:0;min-width:100%;-ms-text-size-adjust:100%;-webkit-text-size-adjust:100% valign=top width=100%>
                                        <tbody>
                                        <tr style=vertical-align:top valign=top>
                                            <td class=divider_inner style=word-break:break-word;vertical-align:top;min-width:100%;-ms-text-size-adjust:100%;-webkit-text-size-adjust:100%;padding-top:0;padding-right:10px;padding-bottom:0;padding-left:10px valign=top>
                                                <table align=center border=0 cellpadding=0 cellspacing=0 class=divider_content role=presentation style="table-layout:fixed;vertical-align:top;border-spacing:0;border-collapse:collapse;mso-table-lspace:0;mso-table-rspace:0;border-top:1px solid #eee;width:100%" valign=top width=100%>
                                                    <tbody>
                                                    <tr style=vertical-align:top valign=top>
                                                        <td style=word-break:break-word;vertical-align:top;-ms-text-size-adjust:100%;-webkit-text-size-adjust:100% valign=top><span></span></td>
                                                    </tr>
                                                    </tbody>
                                                </table>
                                            </td>
                                        </tr>
                                        </tbody>
                                    </table>
                                    <!--[if (!mso)&(!IE)]><!-->
                                </div>
                                <!--<![endif]-->
                            </div>
                        </div>
                        <!--[if (mso)|(IE)]></td></tr></table><![endif]-->
                        <!--[if (mso)|(IE)]></td></tr></table></td></tr></table><![endif]-->
                    </div>
                </div>
            </div>
            <div style=background-color:transparent>
                <div class=block-grid style="min-width:320px;max-width:480px;overflow-wrap:break-word;word-wrap:break-word;word-break:break-word;Margin:0 auto;background-color:#fff">
                    <div style=border-collapse:collapse;display:table;width:100%;background-color:#fff>
                        <!--[if (mso)|(IE)]><table width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:transparent;"><tr><td align="center"><table cellpadding="0" cellspacing="0" border="0" style="width:480px"><tr class="layout-full-width" style="background-color:#ffffff"><![endif]-->
                        <!--[if (mso)|(IE)]><td align="center" width="480" style="background-color:#ffffff;width:480px; border-top: 0px solid transparent; border-left: 0px solid transparent; border-bottom: 0px solid transparent; border-right: 0px solid transparent;" valign="top"><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 0px; padding-left: 0px; padding-top:5px; padding-bottom:5px;"><![endif]-->
                        <div class="col num12" style=min-width:320px;max-width:480px;display:table-cell;vertical-align:top;width:480px>
                            <div class=col_cont style=width:100%!important>
                                <!--[if (!mso)&(!IE)]><!-->
                                <div style="border-top:0 solid transparent;border-left:0 solid transparent;border-bottom:0 solid transparent;border-right:0 solid transparent;padding-top:5px;padding-bottom:5px;padding-right:0;padding-left:0">
                                    <!--<![endif]-->
                                    <!--[if mso]><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 32px; padding-left: 32px; padding-top: 32px; padding-bottom: 24px; font-family: Arial, sans-serif"><![endif]-->
                                    <div style="color:#000;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;line-height:1.5;padding-top:32px;padding-right:32px;padding-bottom:24px;padding-left:32px">
                                        <div style="line-height:1.5;font-size:12px;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;color:#000;mso-line-height-alt:18px">
                                            <p style="font-size:14px;line-height:1.5;word-break:break-word;font-family:Nunito,Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:21px;margin:0">Hello,</p>
                                            <p style="font-size:14px;line-height:1.5;word-break:break-word;font-family:Nunito,Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:21px;margin:0"> </p>
                                            <p style="font-size:14px;line-height:1.5;word-break:break-word;font-family:Nunito,Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:21px;margin:0">A Task needs your approval. Click on the button below to view the Task and approve or reject the request.</p>
                                        </div>
                                    </div>
                                    <!--[if mso]></td></tr></table><![endif]-->
                                    <!--[if (!mso)&(!IE)]><!-->
                                </div>
                                <!--<![endif]-->
                            </div>
                        </div>
                        <!--[if (mso)|(IE)]></td></tr></table><![endif]-->
                        <!--[if (mso)|(IE)]></td></tr></table></td></tr></table><![endif]-->
                    </div>
                </div>
            </div>
            <div style=background-color:transparent>
                <div class=block-grid style="min-width:320px;max-width:480px;overflow-wrap:break-word;word-wrap:break-word;word-break:break-word;Margin:0 auto;background-color:#fff">
                    <div style=border-collapse:collapse;display:table;width:100%;background-color:#fff>
                        <!--[if (mso)|(IE)]><table width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:transparent;"><tr><td align="center"><table cellpadding="0" cellspacing="0" border="0" style="width:480px"><tr class="layout-full-width" style="background-color:#ffffff"><![endif]-->
                        <!--[if (mso)|(IE)]><td align="center" width="480" style="background-color:#ffffff;width:480px; border-top: 0px solid transparent; border-left: 0px solid transparent; border-bottom: 0px solid transparent; border-right: 0px solid transparent;" valign="top"><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 0px; padding-left: 0px; padding-top:5px; padding-bottom:5px;"><![endif]-->
                        <div class="col num12" style=min-width:320px;max-width:480px;display:table-cell;vertical-align:top;width:480px>
                            <div class=col_cont style=width:100%!important>
                                <!--[if (!mso)&(!IE)]><!-->
                                <div style="border-top:0 solid transparent;border-left:0 solid transparent;border-bottom:0 solid transparent;border-right:0 solid transparent;padding-top:5px;padding-bottom:5px;padding-right:0;padding-left:0">
                                    <!--<![endif]-->
                                    <a href=${job} style=text-decoration:none;cursor:pointer>
                                        <div align=left class=button-container style=padding-top:0;padding-right:32px;padding-bottom:24px;padding-left:32px>
                                            <!--[if mso]><table width="100%" cellpadding="0" cellspacing="0" border="0" style="border-spacing: 0; border-collapse: collapse; mso-table-lspace:0pt; mso-table-rspace:0pt;"><tr><td style="padding-top: 0px; padding-right: 32px; padding-bottom: 24px; padding-left: 32px" align="left"><v:roundrect xmlns:v="urn:schemas-microsoft-com:vml" xmlns:w="urn:schemas-microsoft-com:office:word" href="" style="height:39pt; width:123.75pt; v-text-anchor:middle;" arcsize="8%" stroke="false" fillcolor="#1d84ff"><w:anchorlock/><v:textbox inset="0,0,0,0"><center style="color:#ffffff; font-family:Arial, sans-serif; font-size:14px"><![endif]-->
                                            <div style="text-decoration:none;display:inline-block;color:#fff;background-color:#1d84ff;border-radius:4px;-webkit-border-radius:4px;-moz-border-radius:4px;width:auto;width:auto;border-top:0 dotted #fff;border-right:0 dotted #fff;border-bottom:0 dotted #fff;border-left:0 dotted #fff;padding-top:10px;padding-bottom:10px;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;text-align:center;mso-border-alt:none;word-break:keep-all"><span style=padding-left:20px;padding-right:20px;font-size:14px;display:inline-block><span style="font-size:16px;line-height:2;word-break:break-word;font-family:Nunito,Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:32px"><span data-mce-style="font-size: 14px; line-height: 28px;" style=font-size:14px;line-height:28px>Approve Task</span></span></span></div>
                                            <!--[if mso]></center></v:textbox></v:roundrect></td></tr></table><![endif]-->
                                        </div>
                                        <!--[if (!mso)&(!IE)]><!-->
                                    </a></div>

                                <!--<![endif]-->
                            </div>
                        </div>
                        <!--[if (mso)|(IE)]></td></tr></table><![endif]-->
                        <!--[if (mso)|(IE)]></td></tr></table></td></tr></table><![endif]-->
                    </div>
                </div>
            </div>
            <div style=background-color:transparent>
                <div class=block-grid style="min-width:320px;max-width:480px;overflow-wrap:break-word;word-wrap:break-word;word-break:break-word;Margin:0 auto;background-color:#fff">
                    <div style=border-collapse:collapse;display:table;width:100%;background-color:#fff>
                        <!--[if (mso)|(IE)]><table width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:transparent;"><tr><td align="center"><table cellpadding="0" cellspacing="0" border="0" style="width:480px"><tr class="layout-full-width" style="background-color:#ffffff"><![endif]-->
                        <!--[if (mso)|(IE)]><td align="center" width="480" style="background-color:#ffffff;width:480px; border-top: 0px solid transparent; border-left: 0px solid transparent; border-bottom: 0px solid transparent; border-right: 0px solid transparent;" valign="top"><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 0px; padding-left: 0px; padding-top:5px; padding-bottom:5px;"><![endif]-->
                        <div class="col num12" style=min-width:320px;max-width:480px;display:table-cell;vertical-align:top;width:480px>
                            <div class=col_cont style=width:100%!important>
                                <!--[if (!mso)&(!IE)]><!-->
                                <div style="border-top:0 solid transparent;border-left:0 solid transparent;border-bottom:0 solid transparent;border-right:0 solid transparent;padding-top:5px;padding-bottom:5px;padding-right:0;padding-left:0">
                                    <!--<![endif]-->
                                    <!--[if mso]><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 32px; padding-left: 32px; padding-top: 00px; padding-bottom: 32px; font-family: Arial, sans-serif"><![endif]-->
                                    <div style="color:#000;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;line-height:1.5;padding-top:00px;padding-right:32px;padding-bottom:32px;padding-left:32px">
                                        <div style="line-height:1.5;font-size:12px;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;color:#000;mso-line-height-alt:18px">
                                            <p style="font-size:12px;line-height:1.5;word-break:break-word;text-align:left;font-family:Nunito,Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:18px;margin:0"><span style=font-size:12px>Click the above button, or copy and paste the following link on your web browser: <span style=color:#1d84ff>${job}</span></span></p>
                                        </div>
                                    </div>
                                    <!--[if mso]></td></tr></table><![endif]-->
                                    <!--[if (!mso)&(!IE)]><!-->
                                </div>
                                <!--<![endif]-->
                            </div>
                        </div>
                        <!--[if (mso)|(IE)]></td></tr></table><![endif]-->
                        <!--[if (mso)|(IE)]></td></tr></table></td></tr></table><![endif]-->
                    </div>
                </div>
            </div>
            <div style=background-color:transparent>
                <div class=block-grid style="min-width:320px;max-width:480px;overflow-wrap:break-word;word-wrap:break-word;word-break:break-word;Margin:0 auto;background-color:#fff">
                    <div style=border-collapse:collapse;display:table;width:100%;background-color:#fff>
                        <!--[if (mso)|(IE)]><table width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:transparent;"><tr><td align="center"><table cellpadding="0" cellspacing="0" border="0" style="width:480px"><tr class="layout-full-width" style="background-color:#ffffff"><![endif]-->
                        <!--[if (mso)|(IE)]><td align="center" width="480" style="background-color:#ffffff;width:480px; border-top: 0px solid transparent; border-left: 0px solid transparent; border-bottom: 0px solid transparent; border-right: 0px solid transparent;" valign="top"><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 0px; padding-left: 0px; padding-top:5px; padding-bottom:5px;"><![endif]-->
                        <div class="col num12" style=min-width:320px;max-width:480px;display:table-cell;vertical-align:top;width:480px>
                            <div class=col_cont style=width:100%!important>
                                <!--[if (!mso)&(!IE)]><!-->
                                <div style="border-top:0 solid transparent;border-left:0 solid transparent;border-bottom:0 solid transparent;border-right:0 solid transparent;padding-top:5px;padding-bottom:5px;padding-right:0;padding-left:0">
                                    <!--<![endif]-->
                                    <!--[if mso]><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 32px; padding-left: 32px; padding-top: 20px; padding-bottom: 60px; font-family: Arial, sans-serif"><![endif]-->
                                    <div style="color:#000;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;line-height:1.2;padding-top:20px;padding-right:32px;padding-bottom:60px;padding-left:32px">
                                        <div style="line-height:1.2;font-size:12px;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;color:#000;mso-line-height-alt:14px">
                                            <p style="font-size:14px;line-height:1.2;word-break:break-word;font-family:Nunito,Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:17px;margin:0"><span style=font-size:14px>Warm Regards,</span></p>
                                            <p style="font-size:14px;line-height:1.2;word-break:break-word;font-family:Nunito,Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:17px;margin:0"><span style=font-size:14px>CLEEN</span></p>
                                        </div>
                                    </div>
                                    <!--[if mso]></td></tr></table><![endif]-->
                                    <!--[if (!mso)&(!IE)]><!-->
                                </div>
                                <!--<![endif]-->
                            </div>
                        </div>
                        <!--[if (mso)|(IE)]></td></tr></table><![endif]-->
                        <!--[if (mso)|(IE)]></td></tr></table></td></tr></table><![endif]-->
                    </div>
                </div>
            </div>
            <div style=background-color:#f4f4f4>
                <div class=block-grid style="min-width:320px;max-width:480px;overflow-wrap:break-word;word-wrap:break-word;word-break:break-word;Margin:0 auto;background-color:#f4f4f4">
                    <div style=border-collapse:collapse;display:table;width:100%;background-color:#f4f4f4>
                        <!--[if (mso)|(IE)]><table width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f4f4f4;"><tr><td align="center"><table cellpadding="0" cellspacing="0" border="0" style="width:480px"><tr class="layout-full-width" style="background-color:#f4f4f4"><![endif]-->
                        <!--[if (mso)|(IE)]><td align="center" width="480" style="background-color:#f4f4f4;width:480px; border-top: 0px solid transparent; border-left: 0px solid transparent; border-bottom: 0px solid transparent; border-right: 0px solid transparent;" valign="top"><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 0px; padding-left: 0px; padding-top:5px; padding-bottom:5px;"><![endif]-->
                        <div class="col num12" style=min-width:320px;max-width:480px;display:table-cell;vertical-align:top;width:480px>
                            <div class=col_cont style=width:100%!important>
                                <!--[if (!mso)&(!IE)]><!-->
                                <div style="border-top:0 solid transparent;border-left:0 solid transparent;border-bottom:0 solid transparent;border-right:0 solid transparent;padding-top:5px;padding-bottom:5px;padding-right:0;padding-left:0">
                                    <!--<![endif]-->
                                    <!--[if mso]><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 32px; padding-left: 32px; padding-top: 20px; padding-bottom: 20px; font-family: Arial, sans-serif"><![endif]-->
                                    <div style="color:#000;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;line-height:1.2;padding-top:20px;padding-right:32px;padding-bottom:20px;padding-left:32px">
                                        <div style="line-height:1.2;font-size:12px;color:#000;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:14px">
                                            <p style=line-height:1.2;word-break:break-word;text-align:center;mso-line-height-alt:14px;margin:0><span style=color:#000>To log in to CLEEN, open this link in a web browser (e.g. Google Chrome) - <span style=color:#1d84ff>${streemlogin}</span></span></p>
                                        </div>
                                    </div>
                                    <!--[if mso]></td></tr></table><![endif]-->
                                    <!--[if (!mso)&(!IE)]><!-->
                                </div>
                                <!--<![endif]-->
                            </div>
                        </div>
                        <!--[if (mso)|(IE)]></td></tr></table><![endif]-->
                        <!--[if (mso)|(IE)]></td></tr></table></td></tr></table><![endif]-->
                    </div>
                </div>
            </div>
            <!--[if (mso)|(IE)]></td></tr></table><![endif]-->
        </td>
    </tr>
    </tbody>
</table>
<!--[if (IE)]></div><![endif]-->
</body>
</html>');

--changeset sathyam:02-seed-data-7
INSERT INTO email_templates(id, name, content)
VALUES (4, 'AUTHOR_CHECKLIST_CONFIGURATION', '
<!doctype html>
<html xmlns=http://www.w3.org/1999/xhtml xmlns:o=urn:schemas-microsoft-com:office:office xmlns:v=urn:schemas-microsoft-com:vml>
<head>
    <!--[if gte mso 9]><xml><o:OfficeDocumentSettings><o:AllowPNG/><o:PixelsPerInch>96</o:PixelsPerInch></o:OfficeDocumentSettings></xml><![endif]-->
    <meta content="text/html; charset=utf-8" http-equiv=Content-Type>
    <meta content="width=device-width" name=viewport>
    <!--[if !mso]><!-->
    <meta content="IE=edge" http-equiv=X-UA-Compatible>
    <!--<![endif]-->
    <title></title>
    <!--[if !mso]><!-->
    <link href="https://fonts.googleapis.com/css?family=Nunito" rel=stylesheet>
    <!--<![endif]-->
    <style>body{margin:0;padding:0}table,td,tr{vertical-align:top;border-collapse:collapse}*{line-height:inherit}a[x-apple-data-detectors=true]{color:inherit!important;text-decoration:none!important}</style>
    <style id=media-query>@media (max-width:500px){.block-grid,.col{min-width:320px!important;max-width:100%!important;display:block!important}.block-grid{width:100%!important}.col{width:100%!important}.col>div{margin:0 auto}img.fullwidth,img.fullwidthOnMobile{max-width:100%!important}.no-stack .col{min-width:0!important;display:table-cell!important}.no-stack.two-up .col{width:50%!important}.no-stack .col.num2{width:16.6%!important}.no-stack .col.num3{width:25%!important}.no-stack .col.num4{width:33%!important}.no-stack .col.num5{width:41.6%!important}.no-stack .col.num6{width:50%!important}.no-stack .col.num7{width:58.3%!important}.no-stack .col.num8{width:66.6%!important}.no-stack .col.num9{width:75%!important}.no-stack .col.num10{width:83.3%!important}.video-block{max-width:none!important}.mobile_hide{min-height:0;max-height:0;max-width:0;display:none;overflow:hidden;font-size:0}.desktop_hide{display:block!important;max-height:none!important}}</style>
</head>
<body class=clean-body style=margin:0;padding:0;-webkit-text-size-adjust:100%;background-color:#f4f4f4>
<!--[if IE]><div class="ie-browser"><![endif]-->
<table bgcolor=#f4f4f4 cellpadding=0 cellspacing=0 class=nl-container role=presentation style=table-layout:fixed;vertical-align:top;min-width:320px;border-spacing:0;border-collapse:collapse;mso-table-lspace:0;mso-table-rspace:0;background-color:#f4f4f4;width:100% valign=top width=100%>
    <tbody>
    <tr style=vertical-align:top valign=top>
        <td style=word-break:break-word;vertical-align:top valign=top>
            <!--[if (mso)|(IE)]><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td align="center" style="background-color:#f4f4f4"><![endif]-->
            <div style=background-color:#f4f4f4;overflow:hidden>
                <div class=block-grid style="min-width:320px;max-width:480px;overflow-wrap:break-word;word-wrap:break-word;word-break:break-word;Margin:0 auto;width:100%;background-color:#f4f4f4">
                    <div style=border-collapse:collapse;display:table;width:100%;background-color:#f4f4f4>
                        <!--[if (mso)|(IE)]><table width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f4f4f4;"><tr><td align="center"><table cellpadding="0" cellspacing="0" border="0" style="width:480px"><tr class="layout-full-width" style="background-color:#f4f4f4"><![endif]-->
                        <!--[if (mso)|(IE)]><td align="center" width="480" style="background-color:#f4f4f4;width:480px; border-top: 0px solid transparent; border-left: 0px solid transparent; border-bottom: 0px solid transparent; border-right: 0px solid transparent;" valign="top"><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 0px; padding-left: 0px; padding-top:0px; padding-bottom:24px;"><![endif]-->
                        <div class="col num12" style=min-width:320px;max-width:480px;display:table-cell;vertical-align:top;width:480px>
                            <div style=width:100%!important>
                                <!--[if (!mso)&(!IE)]><!-->
                                <div style="border-top:0 solid transparent;border-left:0 solid transparent;border-bottom:0 solid transparent;border-right:0 solid transparent;padding-top:0;padding-bottom:24px;padding-right:0;padding-left:0">
                                    <!--<![endif]-->
                                    <!--[if mso]><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 10px; padding-left: 10px; padding-top: 10px; padding-bottom: 10px; font-family: Arial, sans-serif"><![endif]-->
                                    <div style="color:#393d47;font-family:Arial,Helvetica Neue,Helvetica,sans-serif;line-height:1.2;padding-top:10px;padding-right:10px;padding-bottom:10px;padding-left:10px">
                                        <div style="line-height:1.2;font-size:12px;color:#393d47;font-family:Arial,Helvetica Neue,Helvetica,sans-serif;mso-line-height-alt:14px">
                                            <p style=font-size:14px;line-height:1.2;word-break:break-word;mso-line-height-alt:17px;margin:0> </p>
                                        </div>
                                    </div>
                                    <!--[if mso]></td></tr></table><![endif]-->
                                    <!--[if (!mso)&(!IE)]><!-->
                                </div>
                                <!--<![endif]-->
                            </div>
                        </div>
                        <!--[if (mso)|(IE)]></td></tr></table><![endif]-->
                        <!--[if (mso)|(IE)]></td></tr></table></td></tr></table><![endif]-->
                    </div>
                </div>
            </div>
            <div style=background-color:#f4f4f4;overflow:hidden>
                <div class=block-grid style="min-width:320px;max-width:480px;overflow-wrap:break-word;word-wrap:break-word;word-break:break-word;Margin:0 auto;width:100%;background-color:#fff">
                    <div style=border-collapse:collapse;display:table;width:100%;background-color:#fff>
                        <!--[if (mso)|(IE)]><table width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f4f4f4;"><tr><td align="center"><table cellpadding="0" cellspacing="0" border="0" style="width:480px"><tr class="layout-full-width" style="background-color:#ffffff"><![endif]-->
                        <!--[if (mso)|(IE)]><td align="center" width="480" style="background-color:#ffffff;width:480px; border-top: 0px solid transparent; border-left: 0px solid transparent; border-bottom: 0px solid transparent; border-right: 0px solid transparent;" valign="top"><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 0px; padding-left: 0px; padding-top:32px; padding-bottom:32px;"><![endif]-->
                        <div class="col num12" style=min-width:320px;max-width:480px;display:table-cell;vertical-align:top;width:480px>
                            <div style=width:100%!important>
                                <!--[if (!mso)&(!IE)]><!-->
                                <div style="border-top:0 solid transparent;border-left:0 solid transparent;border-bottom:0 solid transparent;border-right:0 solid transparent;padding-top:32px;padding-bottom:32px;padding-right:0;padding-left:0">
                                    <!--<![endif]-->
                                    <div align=center class="img-container center fixedwidth" style=padding-right:0;padding-left:0>
                                        <!--[if mso]><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr style="line-height:0px"><td style="padding-right: 0px;padding-left: 0px;" align="center"><![endif]--><img align=center alt="Alternate text" border=0 class="center fixedwidth" src=cid:streem-color style=text-decoration:none;-ms-interpolation-mode:bicubic;height:auto;border:0;width:100%;max-width:168px;display:block title="Alternate text" width=168>
                                        <!--[if mso]></td></tr></table><![endif]-->
                                    </div>
                                    <!--[if (!mso)&(!IE)]><!-->
                                </div>
                                <!--<![endif]-->
                            </div>
                        </div>
                        <!--[if (mso)|(IE)]></td></tr></table><![endif]-->
                        <!--[if (mso)|(IE)]></td></tr></table></td></tr></table><![endif]-->
                    </div>
                </div>
            </div>
            <div style=background-color:#f4f4f4;overflow:hidden>
                <div class=block-grid style="min-width:320px;max-width:480px;overflow-wrap:break-word;word-wrap:break-word;word-break:break-word;Margin:0 auto;width:100%;background-color:#fff">
                    <div style=border-collapse:collapse;display:table;width:100%;background-color:#fff>
                        <!--[if (mso)|(IE)]><table width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f4f4f4;"><tr><td align="center"><table cellpadding="0" cellspacing="0" border="0" style="width:480px"><tr class="layout-full-width" style="background-color:#ffffff"><![endif]-->
                        <!--[if (mso)|(IE)]><td align="center" width="480" style="background-color:#ffffff;width:480px; border-top: 0px solid transparent; border-left: 0px solid transparent; border-bottom: 0px solid transparent; border-right: 0px solid transparent;" valign="top"><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 0px; padding-left: 0px; padding-top:5px; padding-bottom:5px;"><![endif]-->
                        <div class="col num12" style=min-width:320px;max-width:480px;display:table-cell;vertical-align:top;width:480px>
                            <div style=width:100%!important>
                                <!--[if (!mso)&(!IE)]><!-->
                                <div style="border-top:0 solid transparent;border-left:0 solid transparent;border-bottom:0 solid transparent;border-right:0 solid transparent;padding-top:5px;padding-bottom:5px;padding-right:0;padding-left:0">
                                    <!--<![endif]-->
                                    <!--[if mso]><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 032px; padding-left: 32px; padding-top: 0px; padding-bottom: 32px; font-family: Arial, sans-serif"><![endif]-->
                                    <div style="color:#000;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;line-height:1.2;padding-top:0;padding-right:32px;padding-bottom:32px;padding-left:32px">
                                        <div style="line-height:1.2;font-size:12px;color:#000;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:14px">
                                            <p style=font-size:20px;line-height:1.2;word-break:break-word;text-align:center;mso-line-height-alt:24px;margin:0><span style=font-size:20px>You are invited to configure a Checklist with CLEEN - Digital Work Instructions</span></p>
                                        </div>
                                    </div>
                                    <!--[if mso]></td></tr></table><![endif]-->
                                    <table border=0 cellpadding=0 cellspacing=0 class=divider role=presentation style=table-layout:fixed;vertical-align:top;border-spacing:0;border-collapse:collapse;mso-table-lspace:0;mso-table-rspace:0;min-width:100%;-ms-text-size-adjust:100%;-webkit-text-size-adjust:100% valign=top width=100%>
                                        <tbody>
                                        <tr style=vertical-align:top valign=top>
                                            <td class=divider_inner style=word-break:break-word;vertical-align:top;min-width:100%;-ms-text-size-adjust:100%;-webkit-text-size-adjust:100%;padding-top:00px;padding-right:00px;padding-bottom:00px;padding-left:00px valign=top>
                                                <table align=center border=0 cellpadding=0 cellspacing=0 class=divider_content role=presentation style="table-layout:fixed;vertical-align:top;border-spacing:0;border-collapse:collapse;mso-table-lspace:0;mso-table-rspace:0;border-top:1px solid #bbb;width:100%" valign=top width=100%>
                                                    <tbody>
                                                    <tr style=vertical-align:top valign=top>
                                                        <td style=word-break:break-word;vertical-align:top;-ms-text-size-adjust:100%;-webkit-text-size-adjust:100% valign=top><span></span></td>
                                                    </tr>
                                                    </tbody>
                                                </table>
                                            </td>
                                        </tr>
                                        </tbody>
                                    </table>
                                    <!--[if (!mso)&(!IE)]><!-->
                                </div>
                                <!--<![endif]-->
                            </div>
                        </div>
                        <!--[if (mso)|(IE)]></td></tr></table><![endif]-->
                        <!--[if (mso)|(IE)]></td></tr></table></td></tr></table><![endif]-->
                    </div>
                </div>
            </div>
            <div style=background-color:transparent;overflow:hidden>
                <div class=block-grid style="min-width:320px;max-width:480px;overflow-wrap:break-word;word-wrap:break-word;word-break:break-word;Margin:0 auto;width:100%;background-color:#fff">
                    <div style=border-collapse:collapse;display:table;width:100%;background-color:#fff>
                        <!--[if (mso)|(IE)]><table width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:transparent;"><tr><td align="center"><table cellpadding="0" cellspacing="0" border="0" style="width:480px"><tr class="layout-full-width" style="background-color:#ffffff"><![endif]-->
                        <!--[if (mso)|(IE)]><td align="center" width="480" style="background-color:#ffffff;width:480px; border-top: 0px solid transparent; border-left: 0px solid transparent; border-bottom: 0px solid transparent; border-right: 0px solid transparent;" valign="top"><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 0px; padding-left: 0px; padding-top:5px; padding-bottom:5px;"><![endif]-->
                        <div class="col num12" style=min-width:320px;max-width:480px;display:table-cell;vertical-align:top;width:480px>
                            <div style=width:100%!important>
                                <!--[if (!mso)&(!IE)]><!-->
                                <div style="border-top:0 solid transparent;border-left:0 solid transparent;border-bottom:0 solid transparent;border-right:0 solid transparent;padding-top:5px;padding-bottom:5px;padding-right:0;padding-left:0">
                                    <!--<![endif]-->
                                    <!--[if mso]><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 32px; padding-left: 32px; padding-top: 32px; padding-bottom: 24px; font-family: Arial, sans-serif"><![endif]-->
                                    <div style="color:#000;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;line-height:1.5;padding-top:32px;padding-right:32px;padding-bottom:24px;padding-left:32px">
                                        <div style="line-height:1.5;font-size:12px;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;color:#000;mso-line-height-alt:18px">
                                            <p style="font-size:14px;line-height:1.5;word-break:break-word;font-family:Nunito,Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:21px;margin:0">Hi!</p>
                                            <p style="font-size:14px;line-height:1.5;word-break:break-word;font-family:Nunito,Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:21px;margin:0"> </p>
                                            <p style="font-size:14px;line-height:1.5;word-break:break-word;font-family:Nunito,Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:21px;margin:0">You are invited you to configure a Checklist on CLEEN-DWI. Please click the button below to start configuring the Checklist:</p>
                                        </div>
                                    </div>
                                    <!--[if mso]></td></tr></table><![endif]-->
                                    <!--[if (!mso)&(!IE)]><!-->
                                </div>
                                <!--<![endif]-->
                            </div>
                        </div>
                        <!--[if (mso)|(IE)]></td></tr></table><![endif]-->
                        <!--[if (mso)|(IE)]></td></tr></table></td></tr></table><![endif]-->
                    </div>
                </div>
            </div>
            <div style=background-color:transparent;overflow:hidden>
                <div class=block-grid style="min-width:320px;max-width:480px;overflow-wrap:break-word;word-wrap:break-word;word-break:break-word;Margin:0 auto;width:100%;background-color:#fff">
                    <div style=border-collapse:collapse;display:table;width:100%;background-color:#fff>
                        <!--[if (mso)|(IE)]><table width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:transparent;"><tr><td align="center"><table cellpadding="0" cellspacing="0" border="0" style="width:480px"><tr class="layout-full-width" style="background-color:#ffffff"><![endif]-->
                        <!--[if (mso)|(IE)]><td align="center" width="480" style="background-color:#ffffff;width:480px; border-top: 0px solid transparent; border-left: 0px solid transparent; border-bottom: 0px solid transparent; border-right: 0px solid transparent;" valign="top"><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 0px; padding-left: 0px; padding-top:5px; padding-bottom:5px;"><![endif]-->
                        <div class="col num12" style=min-width:320px;max-width:480px;display:table-cell;vertical-align:top;width:480px>
                            <div style=width:100%!important>
                                <!--[if (!mso)&(!IE)]><!-->
                                <div style="border-top:0 solid transparent;border-left:0 solid transparent;border-bottom:0 solid transparent;border-right:0 solid transparent;padding-top:5px;padding-bottom:5px;padding-right:0;padding-left:0">
                                    <!--<![endif]-->
                                    <a href=${checklist} style=text-decoration:none;cursor:pointer>
                                        <div align=left class=button-container style=padding-top:0;padding-right:32px;padding-bottom:24px;padding-left:32px>
                                            <!--[if mso]><table width="100%" cellpadding="0" cellspacing="0" border="0" style="border-spacing: 0; border-collapse: collapse; mso-table-lspace:0pt; mso-table-rspace:0pt;"><tr><td style="padding-top: 0px; padding-right: 32px; padding-bottom: 24px; padding-left: 32px" align="left"><v:roundrect xmlns:v="urn:schemas-microsoft-com:vml" xmlns:w="urn:schemas-microsoft-com:office:word" href="" style="height:31.5pt; width:191.25pt; v-text-anchor:middle;" arcsize="10%" stroke="false" fillcolor="#1d84ff"><w:anchorlock/><v:textbox inset="0,0,0,0"><center style="color:#ffffff; font-family:Arial, sans-serif; font-size:16px"><![endif]-->
                                            <div style="text-decoration:none;display:inline-block;color:#fff;background-color:#1d84ff;border-radius:4px;-webkit-border-radius:4px;-moz-border-radius:4px;width:auto;width:auto;border-top:0 dotted #fff;border-right:0 dotted #fff;border-bottom:0 dotted #fff;border-left:0 dotted #fff;padding-top:5px;padding-bottom:5px;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;text-align:center;mso-border-alt:none;word-break:keep-all"><span style=padding-left:32px;padding-right:32px;font-size:16px;display:inline-block><span style="font-size:16px;line-height:2;word-break:break-word;font-family:Nunito,Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:32px">Start Configuring!</span></span></div>
                                            <!--[if mso]></center></v:textbox></v:roundrect></td></tr></table><![endif]-->
                                        </div>
                                    </a>
                                    <!--[if (!mso)&(!IE)]><!-->
                                </div>
                                <!--<![endif]-->
                            </div>
                        </div>
                        <!--[if (mso)|(IE)]></td></tr></table><![endif]-->
                        <!--[if (mso)|(IE)]></td></tr></table></td></tr></table><![endif]-->
                    </div>
                </div>
            </div>
            <div style=background-color:transparent;overflow:hidden>
                <div class=block-grid style="min-width:320px;max-width:480px;overflow-wrap:break-word;word-wrap:break-word;word-break:break-word;Margin:0 auto;width:100%;background-color:#fff">
                    <div style=border-collapse:collapse;display:table;width:100%;background-color:#fff>
                        <!--[if (mso)|(IE)]><table width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:transparent;"><tr><td align="center"><table cellpadding="0" cellspacing="0" border="0" style="width:480px"><tr class="layout-full-width" style="background-color:#ffffff"><![endif]-->
                        <!--[if (mso)|(IE)]><td align="center" width="480" style="background-color:#ffffff;width:480px; border-top: 0px solid transparent; border-left: 0px solid transparent; border-bottom: 0px solid transparent; border-right: 0px solid transparent;" valign="top"><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 0px; padding-left: 0px; padding-top:5px; padding-bottom:5px;"><![endif]-->
                        <div class="col num12" style=min-width:320px;max-width:480px;display:table-cell;vertical-align:top;width:480px>
                            <div style=width:100%!important>
                                <!--[if (!mso)&(!IE)]><!-->
                                <div style="border-top:0 solid transparent;border-left:0 solid transparent;border-bottom:0 solid transparent;border-right:0 solid transparent;padding-top:5px;padding-bottom:5px;padding-right:0;padding-left:0">
                                    <!--<![endif]-->
                                    <!--[if mso]><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 32px; padding-left: 32px; padding-top: 0px; padding-bottom: 32px; font-family: Arial, sans-serif"><![endif]-->
                                    <div style="color:#000;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;line-height:1.5;padding-top:0;padding-right:32px;padding-bottom:32px;padding-left:32px">
                                        <div style="line-height:1.5;font-size:12px;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;color:#000;mso-line-height-alt:18px">
                                            <p style="font-size:14px;line-height:1.5;word-break:break-word;text-align:left;font-family:Nunito,Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:21px;margin:0">Click the above button, or copy and paste the following link on your web browser: ${checklist}</p>
                                        </div>
                                    </div>
                                    <!--[if mso]></td></tr></table><![endif]-->
                                    <!--[if (!mso)&(!IE)]><!-->
                                </div>
                                <!--<![endif]-->
                            </div>
                        </div>
                        <!--[if (mso)|(IE)]></td></tr></table><![endif]-->
                        <!--[if (mso)|(IE)]></td></tr></table></td></tr></table><![endif]-->
                    </div>
                </div>
            </div>
            <div style=background-color:transparent;overflow:hidden>
                <div class=block-grid style="min-width:320px;max-width:480px;overflow-wrap:break-word;word-wrap:break-word;word-break:break-word;Margin:0 auto;width:100%;background-color:#fff">
                    <div style=border-collapse:collapse;display:table;width:100%;background-color:#fff>
                        <!--[if (mso)|(IE)]><table width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:transparent;"><tr><td align="center"><table cellpadding="0" cellspacing="0" border="0" style="width:480px"><tr class="layout-full-width" style="background-color:#ffffff"><![endif]-->
                        <!--[if (mso)|(IE)]><td align="center" width="480" style="background-color:#ffffff;width:480px; border-top: 0px solid transparent; border-left: 0px solid transparent; border-bottom: 0px solid transparent; border-right: 0px solid transparent;" valign="top"><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 0px; padding-left: 0px; padding-top:5px; padding-bottom:5px;"><![endif]-->
                        <div class="col num12" style=min-width:320px;max-width:480px;display:table-cell;vertical-align:top;width:480px>
                            <div style=width:100%!important>
                                <!--[if (!mso)&(!IE)]><!-->
                                <div style="border-top:0 solid transparent;border-left:0 solid transparent;border-bottom:0 solid transparent;border-right:0 solid transparent;padding-top:5px;padding-bottom:5px;padding-right:0;padding-left:0">
                                    <!--<![endif]-->
                                    <!--[if mso]><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 32px; padding-left: 32px; padding-top: 20px; padding-bottom: 20px; font-family: Arial, sans-serif"><![endif]-->
                                    <div style="color:#000;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;line-height:1.2;padding-top:20px;padding-right:32px;padding-bottom:20px;padding-left:32px">
                                        <div style="line-height:1.2;font-size:12px;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;color:#000;mso-line-height-alt:14px">
                                            <p style="font-size:14px;line-height:1.2;word-break:break-word;font-family:Nunito,Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:17px;margin:0"><span style=font-size:14px>Warm Regards,</span></p>
                                            <p style="font-size:14px;line-height:1.2;word-break:break-word;font-family:Nunito,Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:17px;margin:0"><span style=font-size:14px>CLEEN-DWI</span></p>
                                        </div>
                                    </div>
                                    <!--[if mso]></td></tr></table><![endif]-->
                                    <!--[if (!mso)&(!IE)]><!-->
                                </div>
                                <!--<![endif]-->
                            </div>
                        </div>
                        <!--[if (mso)|(IE)]></td></tr></table><![endif]-->
                        <!--[if (mso)|(IE)]></td></tr></table></td></tr></table><![endif]-->
                    </div>
                </div>
            </div>
            <div style=background-color:#f4f4f4;overflow:hidden>
                <div class=block-grid style="min-width:320px;max-width:480px;overflow-wrap:break-word;word-wrap:break-word;word-break:break-word;Margin:0 auto;width:100%;background-color:#f4f4f4">
                    <div style=border-collapse:collapse;display:table;width:100%;background-color:#f4f4f4>
                        <!--[if (mso)|(IE)]><table width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f4f4f4;"><tr><td align="center"><table cellpadding="0" cellspacing="0" border="0" style="width:480px"><tr class="layout-full-width" style="background-color:#f4f4f4"><![endif]-->
                        <!--[if (mso)|(IE)]><td align="center" width="480" style="background-color:#f4f4f4;width:480px; border-top: 0px solid transparent; border-left: 0px solid transparent; border-bottom: 0px solid transparent; border-right: 0px solid transparent;" valign="top"><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 0px; padding-left: 0px; padding-top:5px; padding-bottom:5px;"><![endif]-->
                        <div class="col num12" style=min-width:320px;max-width:480px;display:table-cell;vertical-align:top;width:480px>
                            <div style=width:100%!important>
                                <!--[if (!mso)&(!IE)]><!-->
                                <div style="border-top:0 solid transparent;border-left:0 solid transparent;border-bottom:0 solid transparent;border-right:0 solid transparent;padding-top:5px;padding-bottom:5px;padding-right:0;padding-left:0">
                                    <!--<![endif]-->
                                    <!--[if mso]><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 32px; padding-left: 32px; padding-top: 20px; padding-bottom: 20px; font-family: Arial, sans-serif"><![endif]-->
                                    <div style="color:#000;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;line-height:1.2;padding-top:20px;padding-right:32px;padding-bottom:20px;padding-left:32px">
                                        <div style="line-height:1.2;font-size:12px;color:#000;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:14px">
                                            <p style=line-height:1.2;word-break:break-word;mso-line-height-alt:14px;margin:0><span style=color:#000>To login to the CLEEN App open this link in a web browser (e.g. Google Chrome) - <span style=color:#1d84ff>${streemlogin}</span></span></p>
                                        </div>
                                    </div>
                                    <!--[if mso]></td></tr></table><![endif]-->
                                    <!--[if (!mso)&(!IE)]><!-->
                                </div>
                                <!--<![endif]-->
                            </div>
                        </div>
                        <!--[if (mso)|(IE)]></td></tr></table><![endif]-->
                        <!--[if (mso)|(IE)]></td></tr></table></td></tr></table><![endif]-->
                    </div>
                </div>
            </div>
            <!--[if (mso)|(IE)]></td></tr></table><![endif]-->
        </td>
    </tr>
    </tbody>
</table>
<!--[if (IE)]></div><![endif]-->
</body>
</html>');

--changeset sathyam:02-seed-data-8
INSERT INTO email_templates(id, name, content)
VALUES (5, 'PROTOTYPE_REQUESTED_CHANGES', '
<!doctype html>
<html xmlns=http://www.w3.org/1999/xhtml xmlns:o=urn:schemas-microsoft-com:office:office xmlns:v=urn:schemas-microsoft-com:vml>
<head>
    <!--[if gte mso 9]><xml><o:OfficeDocumentSettings><o:AllowPNG/><o:PixelsPerInch>96</o:PixelsPerInch></o:OfficeDocumentSettings></xml><![endif]-->
    <meta content="text/html; charset=utf-8" http-equiv=Content-Type>
    <meta content="width=device-width" name=viewport>
    <!--[if !mso]><!-->
    <meta content="IE=edge" http-equiv=X-UA-Compatible>
    <!--<![endif]-->
    <title></title>
    <!--[if !mso]><!-->
    <link href="https://fonts.googleapis.com/css?family=Nunito" rel=stylesheet>
    <!--<![endif]-->
    <style>body{margin:0;padding:0}table,td,tr{vertical-align:top;border-collapse:collapse}*{line-height:inherit}a[x-apple-data-detectors=true]{color:inherit!important;text-decoration:none!important}</style>
    <style id=media-query>@media (max-width:500px){.block-grid,.col{min-width:320px!important;max-width:100%!important;display:block!important}.block-grid{width:100%!important}.col{width:100%!important}.col_cont{margin:0 auto}img.fullwidth,img.fullwidthOnMobile{max-width:100%!important}.no-stack .col{min-width:0!important;display:table-cell!important}.no-stack.two-up .col{width:50%!important}.no-stack .col.num2{width:16.6%!important}.no-stack .col.num3{width:25%!important}.no-stack .col.num4{width:33%!important}.no-stack .col.num5{width:41.6%!important}.no-stack .col.num6{width:50%!important}.no-stack .col.num7{width:58.3%!important}.no-stack .col.num8{width:66.6%!important}.no-stack .col.num9{width:75%!important}.no-stack .col.num10{width:83.3%!important}.video-block{max-width:none!important}.mobile_hide{min-height:0;max-height:0;max-width:0;display:none;overflow:hidden;font-size:0}.desktop_hide{display:block!important;max-height:none!important}}</style>
</head>
<body class=clean-body style=margin:0;padding:0;-webkit-text-size-adjust:100%;background-color:#f4f4f4>
<!--[if IE]><div class="ie-browser"><![endif]-->
<table bgcolor=#f4f4f4 cellpadding=0 cellspacing=0 class=nl-container role=presentation style=table-layout:fixed;vertical-align:top;min-width:320px;border-spacing:0;border-collapse:collapse;mso-table-lspace:0;mso-table-rspace:0;background-color:#f4f4f4;width:100% valign=top width=100%>
    <tbody>
    <tr style=vertical-align:top valign=top>
        <td style=word-break:break-word;vertical-align:top valign=top>
            <!--[if (mso)|(IE)]><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td align="center" style="background-color:#f4f4f4"><![endif]-->
            <div style=background-color:#f4f4f4>
                <div class=block-grid style="min-width:320px;max-width:480px;overflow-wrap:break-word;word-wrap:break-word;word-break:break-word;Margin:0 auto;background-color:#f4f4f4">
                    <div style=border-collapse:collapse;display:table;width:100%;background-color:#f4f4f4>
                        <!--[if (mso)|(IE)]><table width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f4f4f4;"><tr><td align="center"><table cellpadding="0" cellspacing="0" border="0" style="width:480px"><tr class="layout-full-width" style="background-color:#f4f4f4"><![endif]-->
                        <!--[if (mso)|(IE)]><td align="center" width="480" style="background-color:#f4f4f4;width:480px; border-top: 0px solid transparent; border-left: 0px solid transparent; border-bottom: 0px solid transparent; border-right: 0px solid transparent;" valign="top"><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 0px; padding-left: 0px; padding-top:50px; padding-bottom:0px;"><![endif]-->
                        <div class="col num12" style=min-width:320px;max-width:480px;display:table-cell;vertical-align:top;width:480px>
                            <div class=col_cont style=width:100%!important>
                                <!--[if (!mso)&(!IE)]><!-->
                                <div style="border-top:0 solid transparent;border-left:0 solid transparent;border-bottom:0 solid transparent;border-right:0 solid transparent;padding-top:50px;padding-bottom:0;padding-right:0;padding-left:0">
                                    <!--<![endif]-->
                                    <div></div>
                                    <!--[if (!mso)&(!IE)]><!-->
                                </div>
                                <!--<![endif]-->
                            </div>
                        </div>
                        <!--[if (mso)|(IE)]></td></tr></table><![endif]-->
                        <!--[if (mso)|(IE)]></td></tr></table></td></tr></table><![endif]-->
                    </div>
                </div>
            </div>
            <div style=background-color:#f4f4f4>
                <div class=block-grid style="min-width:320px;max-width:480px;overflow-wrap:break-word;word-wrap:break-word;word-break:break-word;Margin:0 auto;background-color:#fff">
                    <div style=border-collapse:collapse;display:table;width:100%;background-color:#fff>
                        <!--[if (mso)|(IE)]><table width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f4f4f4;"><tr><td align="center"><table cellpadding="0" cellspacing="0" border="0" style="width:480px"><tr class="layout-full-width" style="background-color:#ffffff"><![endif]-->
                        <!--[if (mso)|(IE)]><td align="center" width="480" style="background-color:#ffffff;width:480px; border-top: 0px solid transparent; border-left: 0px solid transparent; border-bottom: 0px solid transparent; border-right: 0px solid transparent;" valign="top"><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 0px; padding-left: 0px; padding-top:32px; padding-bottom:32px;"><![endif]-->
                        <div class="col num12" style=min-width:320px;max-width:480px;display:table-cell;vertical-align:top;width:480px>
                            <div class=col_cont style=width:100%!important>
                                <!--[if (!mso)&(!IE)]><!-->
                                <div style="border-top:0 solid transparent;border-left:0 solid transparent;border-bottom:0 solid transparent;border-right:0 solid transparent;padding-top:32px;padding-bottom:32px;padding-right:0;padding-left:0">
                                    <!--<![endif]-->
                                    <div align=center class="img-container center fixedwidth" style=padding-right:0;padding-left:0>
                                        <!--[if mso]><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr style="line-height:0px"><td style="padding-right: 0px;padding-left: 0px;" align="center"><![endif]--><img align=center alt="Alternate text" border=0 class="center fixedwidth" src=cid:streem-color style=text-decoration:none;-ms-interpolation-mode:bicubic;height:auto;border:0;width:100%;max-width:144px;display:block title="Alternate text" width=144>
                                        <!--[if mso]></td></tr></table><![endif]-->
                                    </div>
                                    <!--[if (!mso)&(!IE)]><!-->
                                </div>
                                <!--<![endif]-->
                            </div>
                        </div>
                        <!--[if (mso)|(IE)]></td></tr></table><![endif]-->
                        <!--[if (mso)|(IE)]></td></tr></table></td></tr></table><![endif]-->
                    </div>
                </div>
            </div>
            <div style=background-color:#f4f4f4>
                <div class=block-grid style="min-width:320px;max-width:480px;overflow-wrap:break-word;word-wrap:break-word;word-break:break-word;Margin:0 auto;background-color:#fff">
                    <div style=border-collapse:collapse;display:table;width:100%;background-color:#fff>
                        <!--[if (mso)|(IE)]><table width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f4f4f4;"><tr><td align="center"><table cellpadding="0" cellspacing="0" border="0" style="width:480px"><tr class="layout-full-width" style="background-color:#ffffff"><![endif]-->
                        <!--[if (mso)|(IE)]><td align="center" width="480" style="background-color:#ffffff;width:480px; border-top: 0px solid transparent; border-left: 0px solid transparent; border-bottom: 0px solid transparent; border-right: 0px solid transparent;" valign="top"><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 0px; padding-left: 0px; padding-top:5px; padding-bottom:5px;"><![endif]-->
                        <div class="col num12" style=min-width:320px;max-width:480px;display:table-cell;vertical-align:top;width:480px>
                            <div class=col_cont style=width:100%!important>
                                <!--[if (!mso)&(!IE)]><!-->
                                <div style="border-top:0 solid transparent;border-left:0 solid transparent;border-bottom:0 solid transparent;border-right:0 solid transparent;padding-top:5px;padding-bottom:5px;padding-right:0;padding-left:0">
                                    <!--<![endif]-->
                                    <!--[if mso]><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 40px; padding-left: 40px; padding-top: 0px; padding-bottom: 32px; font-family: Arial, sans-serif"><![endif]-->
                                    <div style="color:#000;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;line-height:1.2;padding-top:0;padding-right:40px;padding-bottom:32px;padding-left:40px">
                                        <div style="line-height:1.2;font-size:12px;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;color:#000;mso-line-height-alt:14px">
                                            <p style="font-size:20px;line-height:1.2;word-break:break-word;text-align:center;font-family:Nunito,Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:24px;margin:0"><span style=font-size:20px>Reviewers of a Prototype have requested you to do some changes</span></p>
                                        </div>
                                    </div>
                                    <!--[if mso]></td></tr></table><![endif]-->
                                    <table border=0 cellpadding=0 cellspacing=0 class=divider role=presentation style=table-layout:fixed;vertical-align:top;border-spacing:0;border-collapse:collapse;mso-table-lspace:0;mso-table-rspace:0;min-width:100%;-ms-text-size-adjust:100%;-webkit-text-size-adjust:100% valign=top width=100%>
                                        <tbody>
                                        <tr style=vertical-align:top valign=top>
                                            <td class=divider_inner style=word-break:break-word;vertical-align:top;min-width:100%;-ms-text-size-adjust:100%;-webkit-text-size-adjust:100%;padding-top:0;padding-right:10px;padding-bottom:0;padding-left:10px valign=top>
                                                <table align=center border=0 cellpadding=0 cellspacing=0 class=divider_content role=presentation style="table-layout:fixed;vertical-align:top;border-spacing:0;border-collapse:collapse;mso-table-lspace:0;mso-table-rspace:0;border-top:1px solid #eee;width:100%" valign=top width=100%>
                                                    <tbody>
                                                    <tr style=vertical-align:top valign=top>
                                                        <td style=word-break:break-word;vertical-align:top;-ms-text-size-adjust:100%;-webkit-text-size-adjust:100% valign=top><span></span></td>
                                                    </tr>
                                                    </tbody>
                                                </table>
                                            </td>
                                        </tr>
                                        </tbody>
                                    </table>
                                    <!--[if (!mso)&(!IE)]><!-->
                                </div>
                                <!--<![endif]-->
                            </div>
                        </div>
                        <!--[if (mso)|(IE)]></td></tr></table><![endif]-->
                        <!--[if (mso)|(IE)]></td></tr></table></td></tr></table><![endif]-->
                    </div>
                </div>
            </div>
            <div style=background-color:transparent>
                <div class=block-grid style="min-width:320px;max-width:480px;overflow-wrap:break-word;word-wrap:break-word;word-break:break-word;Margin:0 auto;background-color:#fff">
                    <div style=border-collapse:collapse;display:table;width:100%;background-color:#fff>
                        <!--[if (mso)|(IE)]><table width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:transparent;"><tr><td align="center"><table cellpadding="0" cellspacing="0" border="0" style="width:480px"><tr class="layout-full-width" style="background-color:#ffffff"><![endif]-->
                        <!--[if (mso)|(IE)]><td align="center" width="480" style="background-color:#ffffff;width:480px; border-top: 0px solid transparent; border-left: 0px solid transparent; border-bottom: 0px solid transparent; border-right: 0px solid transparent;" valign="top"><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 0px; padding-left: 0px; padding-top:5px; padding-bottom:5px;"><![endif]-->
                        <div class="col num12" style=min-width:320px;max-width:480px;display:table-cell;vertical-align:top;width:480px>
                            <div class=col_cont style=width:100%!important>
                                <!--[if (!mso)&(!IE)]><!-->
                                <div style="border-top:0 solid transparent;border-left:0 solid transparent;border-bottom:0 solid transparent;border-right:0 solid transparent;padding-top:5px;padding-bottom:5px;padding-right:0;padding-left:0">
                                    <!--<![endif]-->
                                    <!--[if mso]><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 32px; padding-left: 32px; padding-top: 32px; padding-bottom: 24px; font-family: Arial, sans-serif"><![endif]-->
                                    <div style="color:#000;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;line-height:1.5;padding-top:32px;padding-right:32px;padding-bottom:24px;padding-left:32px">
                                        <div style="line-height:1.5;font-size:12px;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;color:#000;mso-line-height-alt:18px">
                                            <p style="font-size:14px;line-height:1.5;word-break:break-word;font-family:Nunito,Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:21px;margin:0">Hello,</p>
                                            <p style="font-size:14px;line-height:1.5;word-break:break-word;font-family:Nunito,Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:21px;margin:0"> </p>
                                            <p style="font-size:14px;line-height:1.5;word-break:break-word;font-family:Nunito,Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:21px;margin:0">All Reviewers of the Prototype have finished reviewing it and have requested you to do some modifications to it. Click the button below to view the changes requested.</p>
                                        </div>
                                    </div>
                                    <!--[if mso]></td></tr></table><![endif]-->
                                    <!--[if (!mso)&(!IE)]><!-->
                                </div>
                                <!--<![endif]-->
                            </div>
                        </div>
                        <!--[if (mso)|(IE)]></td></tr></table><![endif]-->
                        <!--[if (mso)|(IE)]></td></tr></table></td></tr></table><![endif]-->
                    </div>
                </div>
            </div>
            <div style=background-color:transparent>
                <div class=block-grid style="min-width:320px;max-width:480px;overflow-wrap:break-word;word-wrap:break-word;word-break:break-word;Margin:0 auto;background-color:#fff">
                    <div style=border-collapse:collapse;display:table;width:100%;background-color:#fff>
                        <!--[if (mso)|(IE)]><table width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:transparent;"><tr><td align="center"><table cellpadding="0" cellspacing="0" border="0" style="width:480px"><tr class="layout-full-width" style="background-color:#ffffff"><![endif]-->
                        <!--[if (mso)|(IE)]><td align="center" width="480" style="background-color:#ffffff;width:480px; border-top: 0px solid transparent; border-left: 0px solid transparent; border-bottom: 0px solid transparent; border-right: 0px solid transparent;" valign="top"><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 0px; padding-left: 0px; padding-top:5px; padding-bottom:5px;"><![endif]-->
                        <div class="col num12" style=min-width:320px;max-width:480px;display:table-cell;vertical-align:top;width:480px>
                            <div class=col_cont style=width:100%!important>
                                <!--[if (!mso)&(!IE)]><!-->
                                <div style="border-top:0 solid transparent;border-left:0 solid transparent;border-bottom:0 solid transparent;border-right:0 solid transparent;padding-top:5px;padding-bottom:5px;padding-right:0;padding-left:0">
                                    <!--<![endif]-->
                                    <a href=${checklist} style=text-decoration:none;cursor:pointer>
                                        <div align=left class=button-container style=padding-top:0;padding-right:32px;padding-bottom:24px;padding-left:32px>
                                            <!--[if mso]><table width="100%" cellpadding="0" cellspacing="0" border="0" style="border-spacing: 0; border-collapse: collapse; mso-table-lspace:0pt; mso-table-rspace:0pt;"><tr><td style="padding-top: 0px; padding-right: 32px; padding-bottom: 24px; padding-left: 32px" align="left"><v:roundrect xmlns:v="urn:schemas-microsoft-com:vml" xmlns:w="urn:schemas-microsoft-com:office:word" href="" style="height:39pt; width:149.25pt; v-text-anchor:middle;" arcsize="8%" stroke="false" fillcolor="#1d84ff"><w:anchorlock/><v:textbox inset="0,0,0,0"><center style="color:#ffffff; font-family:Arial, sans-serif; font-size:14px"><![endif]-->
                                            <div style="text-decoration:none;display:inline-block;color:#fff;background-color:#1d84ff;border-radius:4px;-webkit-border-radius:4px;-moz-border-radius:4px;width:auto;width:auto;border-top:0 dotted #fff;border-right:0 dotted #fff;border-bottom:0 dotted #fff;border-left:0 dotted #fff;padding-top:10px;padding-bottom:10px;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;text-align:center;mso-border-alt:none;word-break:keep-all"><span style=padding-left:20px;padding-right:20px;font-size:14px;display:inline-block><span style="font-size:16px;line-height:2;word-break:break-word;font-family:Nunito,Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:32px"><span data-mce-style="font-size: 14px; line-height: 28px;" style=font-size:14px;line-height:28px>View Modifications</span></span></span></div>
                                            <!--[if mso]></center></v:textbox></v:roundrect></td></tr></table><![endif]-->
                                        </div>
                                    </a>
                                    <!--[if (!mso)&(!IE)]><!-->
                                </div>
                                <!--<![endif]-->
                            </div>
                        </div>
                        <!--[if (mso)|(IE)]></td></tr></table><![endif]-->
                        <!--[if (mso)|(IE)]></td></tr></table></td></tr></table><![endif]-->
                    </div>
                </div>
            </div>
            <div style=background-color:transparent>
                <div class=block-grid style="min-width:320px;max-width:480px;overflow-wrap:break-word;word-wrap:break-word;word-break:break-word;Margin:0 auto;background-color:#fff">
                    <div style=border-collapse:collapse;display:table;width:100%;background-color:#fff>
                        <!--[if (mso)|(IE)]><table width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:transparent;"><tr><td align="center"><table cellpadding="0" cellspacing="0" border="0" style="width:480px"><tr class="layout-full-width" style="background-color:#ffffff"><![endif]-->
                        <!--[if (mso)|(IE)]><td align="center" width="480" style="background-color:#ffffff;width:480px; border-top: 0px solid transparent; border-left: 0px solid transparent; border-bottom: 0px solid transparent; border-right: 0px solid transparent;" valign="top"><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 0px; padding-left: 0px; padding-top:5px; padding-bottom:5px;"><![endif]-->
                        <div class="col num12" style=min-width:320px;max-width:480px;display:table-cell;vertical-align:top;width:480px>
                            <div class=col_cont style=width:100%!important>
                                <!--[if (!mso)&(!IE)]><!-->
                                <div style="border-top:0 solid transparent;border-left:0 solid transparent;border-bottom:0 solid transparent;border-right:0 solid transparent;padding-top:5px;padding-bottom:5px;padding-right:0;padding-left:0">
                                    <!--<![endif]-->
                                    <!--[if mso]><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 32px; padding-left: 32px; padding-top: 00px; padding-bottom: 32px; font-family: Arial, sans-serif"><![endif]-->
                                    <div style="color:#000;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;line-height:1.5;padding-top:00px;padding-right:32px;padding-bottom:32px;padding-left:32px">
                                        <div style="line-height:1.5;font-size:12px;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;color:#000;mso-line-height-alt:18px">
                                            <p style="font-size:12px;line-height:1.5;word-break:break-word;text-align:left;font-family:Nunito,Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:18px;margin:0"><span style=font-size:12px>Click the above button, or copy and paste the following link on your web browser: <span style=color:#1d84ff>${checklist}</span></span></p>
                                        </div>
                                    </div>
                                    <!--[if mso]></td></tr></table><![endif]-->
                                    <!--[if (!mso)&(!IE)]><!-->
                                </div>
                                <!--<![endif]-->
                            </div>
                        </div>
                        <!--[if (mso)|(IE)]></td></tr></table><![endif]-->
                        <!--[if (mso)|(IE)]></td></tr></table></td></tr></table><![endif]-->
                    </div>
                </div>
            </div>
            <div style=background-color:transparent>
                <div class=block-grid style="min-width:320px;max-width:480px;overflow-wrap:break-word;word-wrap:break-word;word-break:break-word;Margin:0 auto;background-color:#fff">
                    <div style=border-collapse:collapse;display:table;width:100%;background-color:#fff>
                        <!--[if (mso)|(IE)]><table width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:transparent;"><tr><td align="center"><table cellpadding="0" cellspacing="0" border="0" style="width:480px"><tr class="layout-full-width" style="background-color:#ffffff"><![endif]-->
                        <!--[if (mso)|(IE)]><td align="center" width="480" style="background-color:#ffffff;width:480px; border-top: 0px solid transparent; border-left: 0px solid transparent; border-bottom: 0px solid transparent; border-right: 0px solid transparent;" valign="top"><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 0px; padding-left: 0px; padding-top:5px; padding-bottom:5px;"><![endif]-->
                        <div class="col num12" style=min-width:320px;max-width:480px;display:table-cell;vertical-align:top;width:480px>
                            <div class=col_cont style=width:100%!important>
                                <!--[if (!mso)&(!IE)]><!-->
                                <div style="border-top:0 solid transparent;border-left:0 solid transparent;border-bottom:0 solid transparent;border-right:0 solid transparent;padding-top:5px;padding-bottom:5px;padding-right:0;padding-left:0">
                                    <!--<![endif]-->
                                    <!--[if mso]><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 32px; padding-left: 32px; padding-top: 20px; padding-bottom: 60px; font-family: Arial, sans-serif"><![endif]-->
                                    <div style="color:#000;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;line-height:1.2;padding-top:20px;padding-right:32px;padding-bottom:60px;padding-left:32px">
                                        <div style="line-height:1.2;font-size:12px;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;color:#000;mso-line-height-alt:14px">
                                            <p style="font-size:14px;line-height:1.2;word-break:break-word;font-family:Nunito,Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:17px;margin:0"><span style=font-size:14px>Warm Regards,</span></p>
                                            <p style="font-size:14px;line-height:1.2;word-break:break-word;font-family:Nunito,Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:17px;margin:0"><span style=font-size:14px>CLEEN</span></p>
                                        </div>
                                    </div>
                                    <!--[if mso]></td></tr></table><![endif]-->
                                    <!--[if (!mso)&(!IE)]><!-->
                                </div>
                                <!--<![endif]-->
                            </div>
                        </div>
                        <!--[if (mso)|(IE)]></td></tr></table><![endif]-->
                        <!--[if (mso)|(IE)]></td></tr></table></td></tr></table><![endif]-->
                    </div>
                </div>
            </div>
            <div style=background-color:#f4f4f4>
                <div class=block-grid style="min-width:320px;max-width:480px;overflow-wrap:break-word;word-wrap:break-word;word-break:break-word;Margin:0 auto;background-color:#f4f4f4">
                    <div style=border-collapse:collapse;display:table;width:100%;background-color:#f4f4f4>
                        <!--[if (mso)|(IE)]><table width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f4f4f4;"><tr><td align="center"><table cellpadding="0" cellspacing="0" border="0" style="width:480px"><tr class="layout-full-width" style="background-color:#f4f4f4"><![endif]-->
                        <!--[if (mso)|(IE)]><td align="center" width="480" style="background-color:#f4f4f4;width:480px; border-top: 0px solid transparent; border-left: 0px solid transparent; border-bottom: 0px solid transparent; border-right: 0px solid transparent;" valign="top"><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 0px; padding-left: 0px; padding-top:5px; padding-bottom:5px;"><![endif]-->
                        <div class="col num12" style=min-width:320px;max-width:480px;display:table-cell;vertical-align:top;width:480px>
                            <div class=col_cont style=width:100%!important>
                                <!--[if (!mso)&(!IE)]><!-->
                                <div style="border-top:0 solid transparent;border-left:0 solid transparent;border-bottom:0 solid transparent;border-right:0 solid transparent;padding-top:5px;padding-bottom:5px;padding-right:0;padding-left:0">
                                    <!--<![endif]-->
                                    <!--[if mso]><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 32px; padding-left: 32px; padding-top: 20px; padding-bottom: 20px; font-family: Arial, sans-serif"><![endif]-->
                                    <div style="color:#000;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;line-height:1.2;padding-top:20px;padding-right:32px;padding-bottom:20px;padding-left:32px">
                                        <div style="line-height:1.2;font-size:12px;color:#000;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:14px">
                                            <p style=line-height:1.2;word-break:break-word;text-align:center;mso-line-height-alt:14px;margin:0><span style=color:#000>To log in to CLEEN, open this link in a web browser (e.g. Google Chrome) - <span style=color:#1d84ff>${streemlogin}</span></span></p>
                                        </div>
                                    </div>
                                    <!--[if mso]></td></tr></table><![endif]-->
                                    <!--[if (!mso)&(!IE)]><!-->
                                </div>
                                <!--<![endif]-->
                            </div>
                        </div>
                        <!--[if (mso)|(IE)]></td></tr></table><![endif]-->
                        <!--[if (mso)|(IE)]></td></tr></table></td></tr></table><![endif]-->
                    </div>
                </div>
            </div>
            <!--[if (mso)|(IE)]></td></tr></table><![endif]-->
        </td>
    </tr>
    </tbody>
</table>
<!--[if (IE)]></div><![endif]-->
</body>
</html>');

--changeset sathyam:02-seed-data-9
INSERT INTO email_templates(id, name, content)
VALUES (6, 'PROTOTYPE_REVIEW_SUBMIT_REQUEST', '
<!doctype html>
<html xmlns=http://www.w3.org/1999/xhtml xmlns:o=urn:schemas-microsoft-com:office:office xmlns:v=urn:schemas-microsoft-com:vml>
<head>
    <!--[if gte mso 9]><xml><o:OfficeDocumentSettings><o:AllowPNG/><o:PixelsPerInch>96</o:PixelsPerInch></o:OfficeDocumentSettings></xml><![endif]-->
    <meta content="text/html; charset=utf-8" http-equiv=Content-Type>
    <meta content="width=device-width" name=viewport>
    <!--[if !mso]><!-->
    <meta content="IE=edge" http-equiv=X-UA-Compatible>
    <!--<![endif]-->
    <title></title>
    <!--[if !mso]><!-->
    <link href="https://fonts.googleapis.com/css?family=Nunito" rel=stylesheet>
    <!--<![endif]-->
    <style>body{margin:0;padding:0}table,td,tr{vertical-align:top;border-collapse:collapse}*{line-height:inherit}a[x-apple-data-detectors=true]{color:inherit!important;text-decoration:none!important}</style>
    <style id=media-query>@media (max-width:500px){.block-grid,.col{min-width:320px!important;max-width:100%!important;display:block!important}.block-grid{width:100%!important}.col{width:100%!important}.col_cont{margin:0 auto}img.fullwidth,img.fullwidthOnMobile{max-width:100%!important}.no-stack .col{min-width:0!important;display:table-cell!important}.no-stack.two-up .col{width:50%!important}.no-stack .col.num2{width:16.6%!important}.no-stack .col.num3{width:25%!important}.no-stack .col.num4{width:33%!important}.no-stack .col.num5{width:41.6%!important}.no-stack .col.num6{width:50%!important}.no-stack .col.num7{width:58.3%!important}.no-stack .col.num8{width:66.6%!important}.no-stack .col.num9{width:75%!important}.no-stack .col.num10{width:83.3%!important}.video-block{max-width:none!important}.mobile_hide{min-height:0;max-height:0;max-width:0;display:none;overflow:hidden;font-size:0}.desktop_hide{display:block!important;max-height:none!important}}</style>
</head>
<body class=clean-body style=margin:0;padding:0;-webkit-text-size-adjust:100%;background-color:#f4f4f4>
<!--[if IE]><div class="ie-browser"><![endif]-->
<table bgcolor=#f4f4f4 cellpadding=0 cellspacing=0 class=nl-container role=presentation style=table-layout:fixed;vertical-align:top;min-width:320px;border-spacing:0;border-collapse:collapse;mso-table-lspace:0;mso-table-rspace:0;background-color:#f4f4f4;width:100% valign=top width=100%>
    <tbody>
    <tr style=vertical-align:top valign=top>
        <td style=word-break:break-word;vertical-align:top valign=top>
            <!--[if (mso)|(IE)]><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td align="center" style="background-color:#f4f4f4"><![endif]-->
            <div style=background-color:#f4f4f4>
                <div class=block-grid style="min-width:320px;max-width:480px;overflow-wrap:break-word;word-wrap:break-word;word-break:break-word;Margin:0 auto;background-color:#f4f4f4">
                    <div style=border-collapse:collapse;display:table;width:100%;background-color:#f4f4f4>
                        <!--[if (mso)|(IE)]><table width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f4f4f4;"><tr><td align="center"><table cellpadding="0" cellspacing="0" border="0" style="width:480px"><tr class="layout-full-width" style="background-color:#f4f4f4"><![endif]-->
                        <!--[if (mso)|(IE)]><td align="center" width="480" style="background-color:#f4f4f4;width:480px; border-top: 0px solid transparent; border-left: 0px solid transparent; border-bottom: 0px solid transparent; border-right: 0px solid transparent;" valign="top"><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 0px; padding-left: 0px; padding-top:50px; padding-bottom:0px;"><![endif]-->
                        <div class="col num12" style=min-width:320px;max-width:480px;display:table-cell;vertical-align:top;width:480px>
                            <div class=col_cont style=width:100%!important>
                                <!--[if (!mso)&(!IE)]><!-->
                                <div style="border-top:0 solid transparent;border-left:0 solid transparent;border-bottom:0 solid transparent;border-right:0 solid transparent;padding-top:50px;padding-bottom:0;padding-right:0;padding-left:0">
                                    <!--<![endif]-->
                                    <div></div>
                                    <!--[if (!mso)&(!IE)]><!-->
                                </div>
                                <!--<![endif]-->
                            </div>
                        </div>
                        <!--[if (mso)|(IE)]></td></tr></table><![endif]-->
                        <!--[if (mso)|(IE)]></td></tr></table></td></tr></table><![endif]-->
                    </div>
                </div>
            </div>
            <div style=background-color:#f4f4f4>
                <div class=block-grid style="min-width:320px;max-width:480px;overflow-wrap:break-word;word-wrap:break-word;word-break:break-word;Margin:0 auto;background-color:#fff">
                    <div style=border-collapse:collapse;display:table;width:100%;background-color:#fff>
                        <!--[if (mso)|(IE)]><table width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f4f4f4;"><tr><td align="center"><table cellpadding="0" cellspacing="0" border="0" style="width:480px"><tr class="layout-full-width" style="background-color:#ffffff"><![endif]-->
                        <!--[if (mso)|(IE)]><td align="center" width="480" style="background-color:#ffffff;width:480px; border-top: 0px solid transparent; border-left: 0px solid transparent; border-bottom: 0px solid transparent; border-right: 0px solid transparent;" valign="top"><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 0px; padding-left: 0px; padding-top:32px; padding-bottom:32px;"><![endif]-->
                        <div class="col num12" style=min-width:320px;max-width:480px;display:table-cell;vertical-align:top;width:480px>
                            <div class=col_cont style=width:100%!important>
                                <!--[if (!mso)&(!IE)]><!-->
                                <div style="border-top:0 solid transparent;border-left:0 solid transparent;border-bottom:0 solid transparent;border-right:0 solid transparent;padding-top:32px;padding-bottom:32px;padding-right:0;padding-left:0">
                                    <!--<![endif]-->
                                    <div align=center class="img-container center fixedwidth" style=padding-right:0;padding-left:0>
                                        <!--[if mso]><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr style="line-height:0px"><td style="padding-right: 0px;padding-left: 0px;" align="center"><![endif]--><img align=center alt="Alternate text" border=0 class="center fixedwidth" src=cid:streem-color style=text-decoration:none;-ms-interpolation-mode:bicubic;height:auto;border:0;width:100%;max-width:144px;display:block title="Alternate text" width=144>
                                        <!--[if mso]></td></tr></table><![endif]-->
                                    </div>
                                    <!--[if (!mso)&(!IE)]><!-->
                                </div>
                                <!--<![endif]-->
                            </div>
                        </div>
                        <!--[if (mso)|(IE)]></td></tr></table><![endif]-->
                        <!--[if (mso)|(IE)]></td></tr></table></td></tr></table><![endif]-->
                    </div>
                </div>
            </div>
            <div style=background-color:#f4f4f4>
                <div class=block-grid style="min-width:320px;max-width:480px;overflow-wrap:break-word;word-wrap:break-word;word-break:break-word;Margin:0 auto;background-color:#fff">
                    <div style=border-collapse:collapse;display:table;width:100%;background-color:#fff>
                        <!--[if (mso)|(IE)]><table width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f4f4f4;"><tr><td align="center"><table cellpadding="0" cellspacing="0" border="0" style="width:480px"><tr class="layout-full-width" style="background-color:#ffffff"><![endif]-->
                        <!--[if (mso)|(IE)]><td align="center" width="480" style="background-color:#ffffff;width:480px; border-top: 0px solid transparent; border-left: 0px solid transparent; border-bottom: 0px solid transparent; border-right: 0px solid transparent;" valign="top"><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 0px; padding-left: 0px; padding-top:5px; padding-bottom:5px;"><![endif]-->
                        <div class="col num12" style=min-width:320px;max-width:480px;display:table-cell;vertical-align:top;width:480px>
                            <div class=col_cont style=width:100%!important>
                                <!--[if (!mso)&(!IE)]><!-->
                                <div style="border-top:0 solid transparent;border-left:0 solid transparent;border-bottom:0 solid transparent;border-right:0 solid transparent;padding-top:5px;padding-bottom:5px;padding-right:0;padding-left:0">
                                    <!--<![endif]-->
                                    <!--[if mso]><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 40px; padding-left: 40px; padding-top: 0px; padding-bottom: 32px; font-family: Arial, sans-serif"><![endif]-->
                                    <div style="color:#000;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;line-height:1.2;padding-top:0;padding-right:40px;padding-bottom:32px;padding-left:40px">
                                        <div style="line-height:1.2;font-size:12px;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;color:#000;mso-line-height-alt:14px">
                                            <p style="font-size:20px;line-height:1.2;word-break:break-word;text-align:center;font-family:Nunito,Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:24px;margin:0"><span style=font-size:20px>Review cycle of a Prototype has ended. Your confirmation is needed before we intimate the Author(s) for next steps.</span></p>
                                        </div>
                                    </div>
                                    <!--[if mso]></td></tr></table><![endif]-->
                                    <table border=0 cellpadding=0 cellspacing=0 class=divider role=presentation style=table-layout:fixed;vertical-align:top;border-spacing:0;border-collapse:collapse;mso-table-lspace:0;mso-table-rspace:0;min-width:100%;-ms-text-size-adjust:100%;-webkit-text-size-adjust:100% valign=top width=100%>
                                        <tbody>
                                        <tr style=vertical-align:top valign=top>
                                            <td class=divider_inner style=word-break:break-word;vertical-align:top;min-width:100%;-ms-text-size-adjust:100%;-webkit-text-size-adjust:100%;padding-top:0;padding-right:10px;padding-bottom:0;padding-left:10px valign=top>
                                                <table align=center border=0 cellpadding=0 cellspacing=0 class=divider_content role=presentation style="table-layout:fixed;vertical-align:top;border-spacing:0;border-collapse:collapse;mso-table-lspace:0;mso-table-rspace:0;border-top:1px solid #eee;width:100%" valign=top width=100%>
                                                    <tbody>
                                                    <tr style=vertical-align:top valign=top>
                                                        <td style=word-break:break-word;vertical-align:top;-ms-text-size-adjust:100%;-webkit-text-size-adjust:100% valign=top><span></span></td>
                                                    </tr>
                                                    </tbody>
                                                </table>
                                            </td>
                                        </tr>
                                        </tbody>
                                    </table>
                                    <!--[if (!mso)&(!IE)]><!-->
                                </div>
                                <!--<![endif]-->
                            </div>
                        </div>
                        <!--[if (mso)|(IE)]></td></tr></table><![endif]-->
                        <!--[if (mso)|(IE)]></td></tr></table></td></tr></table><![endif]-->
                    </div>
                </div>
            </div>
            <div style=background-color:transparent>
                <div class=block-grid style="min-width:320px;max-width:480px;overflow-wrap:break-word;word-wrap:break-word;word-break:break-word;Margin:0 auto;background-color:#fff">
                    <div style=border-collapse:collapse;display:table;width:100%;background-color:#fff>
                        <!--[if (mso)|(IE)]><table width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:transparent;"><tr><td align="center"><table cellpadding="0" cellspacing="0" border="0" style="width:480px"><tr class="layout-full-width" style="background-color:#ffffff"><![endif]-->
                        <!--[if (mso)|(IE)]><td align="center" width="480" style="background-color:#ffffff;width:480px; border-top: 0px solid transparent; border-left: 0px solid transparent; border-bottom: 0px solid transparent; border-right: 0px solid transparent;" valign="top"><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 0px; padding-left: 0px; padding-top:5px; padding-bottom:5px;"><![endif]-->
                        <div class="col num12" style=min-width:320px;max-width:480px;display:table-cell;vertical-align:top;width:480px>
                            <div class=col_cont style=width:100%!important>
                                <!--[if (!mso)&(!IE)]><!-->
                                <div style="border-top:0 solid transparent;border-left:0 solid transparent;border-bottom:0 solid transparent;border-right:0 solid transparent;padding-top:5px;padding-bottom:5px;padding-right:0;padding-left:0">
                                    <!--<![endif]-->
                                    <!--[if mso]><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 32px; padding-left: 32px; padding-top: 32px; padding-bottom: 24px; font-family: Arial, sans-serif"><![endif]-->
                                    <div style="color:#000;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;line-height:1.5;padding-top:32px;padding-right:32px;padding-bottom:24px;padding-left:32px">
                                        <div style="line-height:1.5;font-size:12px;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;color:#000;mso-line-height-alt:18px">
                                            <p style="font-size:14px;line-height:1.5;word-break:break-word;font-family:Nunito,Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:21px;margin:0">Hello,</p>
                                            <p style="font-size:14px;line-height:1.5;word-break:break-word;font-family:Nunito,Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:21px;margin:0"> </p>
                                            <p style="font-size:14px;line-height:1.5;word-break:break-word;font-family:Nunito,Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:21px;margin:0">All participating Reviewers have completed their side of the review of a Prototype. We are now asking all the Reviewers for confirmation before we intimate the Author(s) for the Next Steps. Click the link below to provide your confirmation.</p>
                                        </div>
                                    </div>
                                    <!--[if mso]></td></tr></table><![endif]-->
                                    <!--[if (!mso)&(!IE)]><!-->
                                </div>
                                <!--<![endif]-->
                            </div>
                        </div>
                        <!--[if (mso)|(IE)]></td></tr></table><![endif]-->
                        <!--[if (mso)|(IE)]></td></tr></table></td></tr></table><![endif]-->
                    </div>
                </div>
            </div>
            <div style=background-color:transparent>
                <div class=block-grid style="min-width:320px;max-width:480px;overflow-wrap:break-word;word-wrap:break-word;word-break:break-word;Margin:0 auto;background-color:#fff">
                    <div style=border-collapse:collapse;display:table;width:100%;background-color:#fff>
                        <!--[if (mso)|(IE)]><table width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:transparent;"><tr><td align="center"><table cellpadding="0" cellspacing="0" border="0" style="width:480px"><tr class="layout-full-width" style="background-color:#ffffff"><![endif]-->
                        <!--[if (mso)|(IE)]><td align="center" width="480" style="background-color:#ffffff;width:480px; border-top: 0px solid transparent; border-left: 0px solid transparent; border-bottom: 0px solid transparent; border-right: 0px solid transparent;" valign="top"><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 0px; padding-left: 0px; padding-top:5px; padding-bottom:5px;"><![endif]-->
                        <div class="col num12" style=min-width:320px;max-width:480px;display:table-cell;vertical-align:top;width:480px>
                            <div class=col_cont style=width:100%!important>
                                <!--[if (!mso)&(!IE)]><!-->
                                <div style="border-top:0 solid transparent;border-left:0 solid transparent;border-bottom:0 solid transparent;border-right:0 solid transparent;padding-top:5px;padding-bottom:5px;padding-right:0;padding-left:0">
                                    <!--<![endif]-->
                                    <a href=${checklist} style=text-decoration:none;cursor:pointer>
                                        <div align=left class=button-container style=padding-top:0;padding-right:32px;padding-bottom:24px;padding-left:32px>
                                            <!--[if mso]><table width="100%" cellpadding="0" cellspacing="0" border="0" style="border-spacing: 0; border-collapse: collapse; mso-table-lspace:0pt; mso-table-rspace:0pt;"><tr><td style="padding-top: 0px; padding-right: 32px; padding-bottom: 24px; padding-left: 32px" align="left"><v:roundrect xmlns:v="urn:schemas-microsoft-com:vml" xmlns:w="urn:schemas-microsoft-com:office:word" href="" style="height:39pt; width:125.25pt; v-text-anchor:middle;" arcsize="8%" stroke="false" fillcolor="#1d84ff"><w:anchorlock/><v:textbox inset="0,0,0,0"><center style="color:#ffffff; font-family:Arial, sans-serif; font-size:14px"><![endif]-->
                                            <div style="text-decoration:none;display:inline-block;color:#fff;background-color:#1d84ff;border-radius:4px;-webkit-border-radius:4px;-moz-border-radius:4px;width:auto;width:auto;border-top:0 dotted #fff;border-right:0 dotted #fff;border-bottom:0 dotted #fff;border-left:0 dotted #fff;padding-top:10px;padding-bottom:10px;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;text-align:center;mso-border-alt:none;word-break:keep-all"><span style=padding-left:20px;padding-right:20px;font-size:14px;display:inline-block><span style="font-size:16px;line-height:2;word-break:break-word;font-family:Nunito,Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:32px"><span data-mce-style="font-size: 14px; line-height: 28px;" style=font-size:14px;line-height:28px>Finish Review</span></span></span></div>
                                            <!--[if mso]></center></v:textbox></v:roundrect></td></tr></table><![endif]-->
                                        </div>
                                    </a>
                                    <!--[if (!mso)&(!IE)]><!-->
                                </div>
                                <!--<![endif]-->
                            </div>
                        </div>
                        <!--[if (mso)|(IE)]></td></tr></table><![endif]-->
                        <!--[if (mso)|(IE)]></td></tr></table></td></tr></table><![endif]-->
                    </div>
                </div>
            </div>
            <div style=background-color:transparent>
                <div class=block-grid style="min-width:320px;max-width:480px;overflow-wrap:break-word;word-wrap:break-word;word-break:break-word;Margin:0 auto;background-color:#fff">
                    <div style=border-collapse:collapse;display:table;width:100%;background-color:#fff>
                        <!--[if (mso)|(IE)]><table width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:transparent;"><tr><td align="center"><table cellpadding="0" cellspacing="0" border="0" style="width:480px"><tr class="layout-full-width" style="background-color:#ffffff"><![endif]-->
                        <!--[if (mso)|(IE)]><td align="center" width="480" style="background-color:#ffffff;width:480px; border-top: 0px solid transparent; border-left: 0px solid transparent; border-bottom: 0px solid transparent; border-right: 0px solid transparent;" valign="top"><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 0px; padding-left: 0px; padding-top:5px; padding-bottom:5px;"><![endif]-->
                        <div class="col num12" style=min-width:320px;max-width:480px;display:table-cell;vertical-align:top;width:480px>
                            <div class=col_cont style=width:100%!important>
                                <!--[if (!mso)&(!IE)]><!-->
                                <div style="border-top:0 solid transparent;border-left:0 solid transparent;border-bottom:0 solid transparent;border-right:0 solid transparent;padding-top:5px;padding-bottom:5px;padding-right:0;padding-left:0">
                                    <!--<![endif]-->
                                    <!--[if mso]><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 32px; padding-left: 32px; padding-top: 00px; padding-bottom: 32px; font-family: Arial, sans-serif"><![endif]-->
                                    <div style="color:#000;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;line-height:1.5;padding-top:00px;padding-right:32px;padding-bottom:32px;padding-left:32px">
                                        <div style="line-height:1.5;font-size:12px;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;color:#000;mso-line-height-alt:18px">
                                            <p style="font-size:12px;line-height:1.5;word-break:break-word;text-align:left;font-family:Nunito,Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:18px;margin:0"><span style=font-size:12px>Click the above button, or copy and paste the following link on your web browser: <span style=color:#1d84ff>${checklist}</span></span></p>
                                        </div>
                                    </div>
                                    <!--[if mso]></td></tr></table><![endif]-->
                                    <!--[if (!mso)&(!IE)]><!-->
                                </div>
                                <!--<![endif]-->
                            </div>
                        </div>
                        <!--[if (mso)|(IE)]></td></tr></table><![endif]-->
                        <!--[if (mso)|(IE)]></td></tr></table></td></tr></table><![endif]-->
                    </div>
                </div>
            </div>
            <div style=background-color:transparent>
                <div class=block-grid style="min-width:320px;max-width:480px;overflow-wrap:break-word;word-wrap:break-word;word-break:break-word;Margin:0 auto;background-color:#fff">
                    <div style=border-collapse:collapse;display:table;width:100%;background-color:#fff>
                        <!--[if (mso)|(IE)]><table width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:transparent;"><tr><td align="center"><table cellpadding="0" cellspacing="0" border="0" style="width:480px"><tr class="layout-full-width" style="background-color:#ffffff"><![endif]-->
                        <!--[if (mso)|(IE)]><td align="center" width="480" style="background-color:#ffffff;width:480px; border-top: 0px solid transparent; border-left: 0px solid transparent; border-bottom: 0px solid transparent; border-right: 0px solid transparent;" valign="top"><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 0px; padding-left: 0px; padding-top:5px; padding-bottom:5px;"><![endif]-->
                        <div class="col num12" style=min-width:320px;max-width:480px;display:table-cell;vertical-align:top;width:480px>
                            <div class=col_cont style=width:100%!important>
                                <!--[if (!mso)&(!IE)]><!-->
                                <div style="border-top:0 solid transparent;border-left:0 solid transparent;border-bottom:0 solid transparent;border-right:0 solid transparent;padding-top:5px;padding-bottom:5px;padding-right:0;padding-left:0">
                                    <!--<![endif]-->
                                    <!--[if mso]><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 32px; padding-left: 32px; padding-top: 20px; padding-bottom: 60px; font-family: Arial, sans-serif"><![endif]-->
                                    <div style="color:#000;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;line-height:1.2;padding-top:20px;padding-right:32px;padding-bottom:60px;padding-left:32px">
                                        <div style="line-height:1.2;font-size:12px;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;color:#000;mso-line-height-alt:14px">
                                            <p style="font-size:14px;line-height:1.2;word-break:break-word;font-family:Nunito,Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:17px;margin:0"><span style=font-size:14px>Warm Regards,</span></p>
                                            <p style="font-size:14px;line-height:1.2;word-break:break-word;font-family:Nunito,Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:17px;margin:0"><span style=font-size:14px>CLEEN</span></p>
                                        </div>
                                    </div>
                                    <!--[if mso]></td></tr></table><![endif]-->
                                    <!--[if (!mso)&(!IE)]><!-->
                                </div>
                                <!--<![endif]-->
                            </div>
                        </div>
                        <!--[if (mso)|(IE)]></td></tr></table><![endif]-->
                        <!--[if (mso)|(IE)]></td></tr></table></td></tr></table><![endif]-->
                    </div>
                </div>
            </div>
            <div style=background-color:#f4f4f4>
                <div class=block-grid style="min-width:320px;max-width:480px;overflow-wrap:break-word;word-wrap:break-word;word-break:break-word;Margin:0 auto;background-color:#f4f4f4">
                    <div style=border-collapse:collapse;display:table;width:100%;background-color:#f4f4f4>
                        <!--[if (mso)|(IE)]><table width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f4f4f4;"><tr><td align="center"><table cellpadding="0" cellspacing="0" border="0" style="width:480px"><tr class="layout-full-width" style="background-color:#f4f4f4"><![endif]-->
                        <!--[if (mso)|(IE)]><td align="center" width="480" style="background-color:#f4f4f4;width:480px; border-top: 0px solid transparent; border-left: 0px solid transparent; border-bottom: 0px solid transparent; border-right: 0px solid transparent;" valign="top"><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 0px; padding-left: 0px; padding-top:5px; padding-bottom:5px;"><![endif]-->
                        <div class="col num12" style=min-width:320px;max-width:480px;display:table-cell;vertical-align:top;width:480px>
                            <div class=col_cont style=width:100%!important>
                                <!--[if (!mso)&(!IE)]><!-->
                                <div style="border-top:0 solid transparent;border-left:0 solid transparent;border-bottom:0 solid transparent;border-right:0 solid transparent;padding-top:5px;padding-bottom:5px;padding-right:0;padding-left:0">
                                    <!--<![endif]-->
                                    <!--[if mso]><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 32px; padding-left: 32px; padding-top: 20px; padding-bottom: 20px; font-family: Arial, sans-serif"><![endif]-->
                                    <div style="color:#000;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;line-height:1.2;padding-top:20px;padding-right:32px;padding-bottom:20px;padding-left:32px">
                                        <div style="line-height:1.2;font-size:12px;color:#000;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:14px">
                                            <p style=line-height:1.2;word-break:break-word;text-align:center;mso-line-height-alt:14px;margin:0><span style=color:#000>To log in to CLEEN, open this link in a web browser (e.g. Google Chrome) - <span style=color:#1d84ff>${streemlogin}</span></span></p>
                                        </div>
                                    </div>
                                    <!--[if mso]></td></tr></table><![endif]-->
                                    <!--[if (!mso)&(!IE)]><!-->
                                </div>
                                <!--<![endif]-->
                            </div>
                        </div>
                        <!--[if (mso)|(IE)]></td></tr></table><![endif]-->
                        <!--[if (mso)|(IE)]></td></tr></table></td></tr></table><![endif]-->
                    </div>
                </div>
            </div>
            <!--[if (mso)|(IE)]></td></tr></table><![endif]-->
        </td>
    </tr>
    </tbody>
</table>
<!--[if (IE)]></div><![endif]-->
</body>
</html>');

--changeset sathyam:02-seed-data-10
INSERT INTO email_templates(id, name, content)
VALUES (7, 'REVIEWER_ASSIGNED_TO_CHECKLIST', '
<!doctype html>
<html xmlns=http://www.w3.org/1999/xhtml xmlns:o=urn:schemas-microsoft-com:office:office xmlns:v=urn:schemas-microsoft-com:vml>
<head>
    <!--[if gte mso 9]><xml><o:OfficeDocumentSettings><o:AllowPNG/><o:PixelsPerInch>96</o:PixelsPerInch></o:OfficeDocumentSettings></xml><![endif]-->
    <meta content="text/html; charset=utf-8" http-equiv=Content-Type>
    <meta content="width=device-width" name=viewport>
    <!--[if !mso]><!-->
    <meta content="IE=edge" http-equiv=X-UA-Compatible>
    <!--<![endif]-->
    <title></title>
    <!--[if !mso]><!-->
    <link href="https://fonts.googleapis.com/css?family=Nunito" rel=stylesheet>
    <!--<![endif]-->
    <style>body{margin:0;padding:0}table,td,tr{vertical-align:top;border-collapse:collapse}*{line-height:inherit}a[x-apple-data-detectors=true]{color:inherit!important;text-decoration:none!important}</style>
    <style id=media-query>@media (max-width:500px){.block-grid,.col{min-width:320px!important;max-width:100%!important;display:block!important}.block-grid{width:100%!important}.col{width:100%!important}.col_cont{margin:0 auto}img.fullwidth,img.fullwidthOnMobile{max-width:100%!important}.no-stack .col{min-width:0!important;display:table-cell!important}.no-stack.two-up .col{width:50%!important}.no-stack .col.num2{width:16.6%!important}.no-stack .col.num3{width:25%!important}.no-stack .col.num4{width:33%!important}.no-stack .col.num5{width:41.6%!important}.no-stack .col.num6{width:50%!important}.no-stack .col.num7{width:58.3%!important}.no-stack .col.num8{width:66.6%!important}.no-stack .col.num9{width:75%!important}.no-stack .col.num10{width:83.3%!important}.video-block{max-width:none!important}.mobile_hide{min-height:0;max-height:0;max-width:0;display:none;overflow:hidden;font-size:0}.desktop_hide{display:block!important;max-height:none!important}}</style>
</head>
<body class=clean-body style=margin:0;padding:0;-webkit-text-size-adjust:100%;background-color:#f4f4f4>
<!--[if IE]><div class="ie-browser"><![endif]-->
<table bgcolor=#f4f4f4 cellpadding=0 cellspacing=0 class=nl-container role=presentation style=table-layout:fixed;vertical-align:top;min-width:320px;border-spacing:0;border-collapse:collapse;mso-table-lspace:0;mso-table-rspace:0;background-color:#f4f4f4;width:100% valign=top width=100%>
    <tbody>
    <tr style=vertical-align:top valign=top>
        <td style=word-break:break-word;vertical-align:top valign=top>
            <!--[if (mso)|(IE)]><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td align="center" style="background-color:#f4f4f4"><![endif]-->
            <div style=background-color:#f4f4f4>
                <div class=block-grid style="min-width:320px;max-width:480px;overflow-wrap:break-word;word-wrap:break-word;word-break:break-word;Margin:0 auto;background-color:#f4f4f4">
                    <div style=border-collapse:collapse;display:table;width:100%;background-color:#f4f4f4>
                        <!--[if (mso)|(IE)]><table width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f4f4f4;"><tr><td align="center"><table cellpadding="0" cellspacing="0" border="0" style="width:480px"><tr class="layout-full-width" style="background-color:#f4f4f4"><![endif]-->
                        <!--[if (mso)|(IE)]><td align="center" width="480" style="background-color:#f4f4f4;width:480px; border-top: 0px solid transparent; border-left: 0px solid transparent; border-bottom: 0px solid transparent; border-right: 0px solid transparent;" valign="top"><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 0px; padding-left: 0px; padding-top:50px; padding-bottom:0px;"><![endif]-->
                        <div class="col num12" style=min-width:320px;max-width:480px;display:table-cell;vertical-align:top;width:480px>
                            <div class=col_cont style=width:100%!important>
                                <!--[if (!mso)&(!IE)]><!-->
                                <div style="border-top:0 solid transparent;border-left:0 solid transparent;border-bottom:0 solid transparent;border-right:0 solid transparent;padding-top:50px;padding-bottom:0;padding-right:0;padding-left:0">
                                    <!--<![endif]-->
                                    <div></div>
                                    <!--[if (!mso)&(!IE)]><!-->
                                </div>
                                <!--<![endif]-->
                            </div>
                        </div>
                        <!--[if (mso)|(IE)]></td></tr></table><![endif]-->
                        <!--[if (mso)|(IE)]></td></tr></table></td></tr></table><![endif]-->
                    </div>
                </div>
            </div>
            <div style=background-color:#f4f4f4>
                <div class=block-grid style="min-width:320px;max-width:480px;overflow-wrap:break-word;word-wrap:break-word;word-break:break-word;Margin:0 auto;background-color:#fff">
                    <div style=border-collapse:collapse;display:table;width:100%;background-color:#fff>
                        <!--[if (mso)|(IE)]><table width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f4f4f4;"><tr><td align="center"><table cellpadding="0" cellspacing="0" border="0" style="width:480px"><tr class="layout-full-width" style="background-color:#ffffff"><![endif]-->
                        <!--[if (mso)|(IE)]><td align="center" width="480" style="background-color:#ffffff;width:480px; border-top: 0px solid transparent; border-left: 0px solid transparent; border-bottom: 0px solid transparent; border-right: 0px solid transparent;" valign="top"><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 0px; padding-left: 0px; padding-top:32px; padding-bottom:32px;"><![endif]-->
                        <div class="col num12" style=min-width:320px;max-width:480px;display:table-cell;vertical-align:top;width:480px>
                            <div class=col_cont style=width:100%!important>
                                <!--[if (!mso)&(!IE)]><!-->
                                <div style="border-top:0 solid transparent;border-left:0 solid transparent;border-bottom:0 solid transparent;border-right:0 solid transparent;padding-top:32px;padding-bottom:32px;padding-right:0;padding-left:0">
                                    <!--<![endif]-->
                                    <div align=center class="img-container center fixedwidth" style=padding-right:0;padding-left:0>
                                        <!--[if mso]><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr style="line-height:0px"><td style="padding-right: 0px;padding-left: 0px;" align="center"><![endif]--><img align=center alt="Alternate text" border=0 class="center fixedwidth" src=cid:streem-color style=text-decoration:none;-ms-interpolation-mode:bicubic;height:auto;border:0;width:100%;max-width:144px;display:block title="Alternate text" width=144>
                                        <!--[if mso]></td></tr></table><![endif]-->
                                    </div>
                                    <!--[if (!mso)&(!IE)]><!-->
                                </div>
                                <!--<![endif]-->
                            </div>
                        </div>
                        <!--[if (mso)|(IE)]></td></tr></table><![endif]-->
                        <!--[if (mso)|(IE)]></td></tr></table></td></tr></table><![endif]-->
                    </div>
                </div>
            </div>
            <div style=background-color:#f4f4f4>
                <div class=block-grid style="min-width:320px;max-width:480px;overflow-wrap:break-word;word-wrap:break-word;word-break:break-word;Margin:0 auto;background-color:#fff">
                    <div style=border-collapse:collapse;display:table;width:100%;background-color:#fff>
                        <!--[if (mso)|(IE)]><table width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f4f4f4;"><tr><td align="center"><table cellpadding="0" cellspacing="0" border="0" style="width:480px"><tr class="layout-full-width" style="background-color:#ffffff"><![endif]-->
                        <!--[if (mso)|(IE)]><td align="center" width="480" style="background-color:#ffffff;width:480px; border-top: 0px solid transparent; border-left: 0px solid transparent; border-bottom: 0px solid transparent; border-right: 0px solid transparent;" valign="top"><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 0px; padding-left: 0px; padding-top:5px; padding-bottom:5px;"><![endif]-->
                        <div class="col num12" style=min-width:320px;max-width:480px;display:table-cell;vertical-align:top;width:480px>
                            <div class=col_cont style=width:100%!important>
                                <!--[if (!mso)&(!IE)]><!-->
                                <div style="border-top:0 solid transparent;border-left:0 solid transparent;border-bottom:0 solid transparent;border-right:0 solid transparent;padding-top:5px;padding-bottom:5px;padding-right:0;padding-left:0">
                                    <!--<![endif]-->
                                    <!--[if mso]><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 40px; padding-left: 40px; padding-top: 0px; padding-bottom: 32px; font-family: Arial, sans-serif"><![endif]-->
                                    <div style="color:#000;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;line-height:1.2;padding-top:0;padding-right:40px;padding-bottom:32px;padding-left:40px">
                                        <div style="line-height:1.2;font-size:12px;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;color:#000;mso-line-height-alt:14px">
                                            <p style="font-size:20px;line-height:1.2;word-break:break-word;text-align:center;font-family:Nunito,Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:24px;margin:0"><span style=font-size:20px>The Author(s) of the Prototype have submitted a Prototype for your Review</span></p>
                                        </div>
                                    </div>
                                    <!--[if mso]></td></tr></table><![endif]-->
                                    <table border=0 cellpadding=0 cellspacing=0 class=divider role=presentation style=table-layout:fixed;vertical-align:top;border-spacing:0;border-collapse:collapse;mso-table-lspace:0;mso-table-rspace:0;min-width:100%;-ms-text-size-adjust:100%;-webkit-text-size-adjust:100% valign=top width=100%>
                                        <tbody>
                                        <tr style=vertical-align:top valign=top>
                                            <td class=divider_inner style=word-break:break-word;vertical-align:top;min-width:100%;-ms-text-size-adjust:100%;-webkit-text-size-adjust:100%;padding-top:0;padding-right:10px;padding-bottom:0;padding-left:10px valign=top>
                                                <table align=center border=0 cellpadding=0 cellspacing=0 class=divider_content role=presentation style="table-layout:fixed;vertical-align:top;border-spacing:0;border-collapse:collapse;mso-table-lspace:0;mso-table-rspace:0;border-top:1px solid #eee;width:100%" valign=top width=100%>
                                                    <tbody>
                                                    <tr style=vertical-align:top valign=top>
                                                        <td style=word-break:break-word;vertical-align:top;-ms-text-size-adjust:100%;-webkit-text-size-adjust:100% valign=top><span></span></td>
                                                    </tr>
                                                    </tbody>
                                                </table>
                                            </td>
                                        </tr>
                                        </tbody>
                                    </table>
                                    <!--[if (!mso)&(!IE)]><!-->
                                </div>
                                <!--<![endif]-->
                            </div>
                        </div>
                        <!--[if (mso)|(IE)]></td></tr></table><![endif]-->
                        <!--[if (mso)|(IE)]></td></tr></table></td></tr></table><![endif]-->
                    </div>
                </div>
            </div>
            <div style=background-color:transparent>
                <div class=block-grid style="min-width:320px;max-width:480px;overflow-wrap:break-word;word-wrap:break-word;word-break:break-word;Margin:0 auto;background-color:#fff">
                    <div style=border-collapse:collapse;display:table;width:100%;background-color:#fff>
                        <!--[if (mso)|(IE)]><table width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:transparent;"><tr><td align="center"><table cellpadding="0" cellspacing="0" border="0" style="width:480px"><tr class="layout-full-width" style="background-color:#ffffff"><![endif]-->
                        <!--[if (mso)|(IE)]><td align="center" width="480" style="background-color:#ffffff;width:480px; border-top: 0px solid transparent; border-left: 0px solid transparent; border-bottom: 0px solid transparent; border-right: 0px solid transparent;" valign="top"><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 0px; padding-left: 0px; padding-top:5px; padding-bottom:5px;"><![endif]-->
                        <div class="col num12" style=min-width:320px;max-width:480px;display:table-cell;vertical-align:top;width:480px>
                            <div class=col_cont style=width:100%!important>
                                <!--[if (!mso)&(!IE)]><!-->
                                <div style="border-top:0 solid transparent;border-left:0 solid transparent;border-bottom:0 solid transparent;border-right:0 solid transparent;padding-top:5px;padding-bottom:5px;padding-right:0;padding-left:0">
                                    <!--<![endif]-->
                                    <!--[if mso]><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 32px; padding-left: 32px; padding-top: 32px; padding-bottom: 24px; font-family: Arial, sans-serif"><![endif]-->
                                    <div style="color:#000;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;line-height:1.5;padding-top:32px;padding-right:32px;padding-bottom:24px;padding-left:32px">
                                        <div style="line-height:1.5;font-size:12px;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;color:#000;mso-line-height-alt:18px">
                                            <p style="font-size:14px;line-height:1.5;word-break:break-word;font-family:Nunito,Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:21px;margin:0">Hello,</p>
                                            <p style="font-size:14px;line-height:1.5;word-break:break-word;font-family:Nunito,Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:21px;margin:0"> </p>
                                            <p style="font-size:14px;line-height:1.5;word-break:break-word;font-family:Nunito,Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:21px;margin:0">A Prototype has been submitted by the Authors for your Review. Click on the button below to view the Prototype and begin your Review.</p>
                                        </div>
                                    </div>
                                    <!--[if mso]></td></tr></table><![endif]-->
                                    <!--[if (!mso)&(!IE)]><!-->
                                </div>
                                <!--<![endif]-->
                            </div>
                        </div>
                        <!--[if (mso)|(IE)]></td></tr></table><![endif]-->
                        <!--[if (mso)|(IE)]></td></tr></table></td></tr></table><![endif]-->
                    </div>
                </div>
            </div>
            <div style=background-color:transparent>
                <div class=block-grid style="min-width:320px;max-width:480px;overflow-wrap:break-word;word-wrap:break-word;word-break:break-word;Margin:0 auto;background-color:#fff">
                    <div style=border-collapse:collapse;display:table;width:100%;background-color:#fff>
                        <!--[if (mso)|(IE)]><table width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:transparent;"><tr><td align="center"><table cellpadding="0" cellspacing="0" border="0" style="width:480px"><tr class="layout-full-width" style="background-color:#ffffff"><![endif]-->
                        <!--[if (mso)|(IE)]><td align="center" width="480" style="background-color:#ffffff;width:480px; border-top: 0px solid transparent; border-left: 0px solid transparent; border-bottom: 0px solid transparent; border-right: 0px solid transparent;" valign="top"><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 0px; padding-left: 0px; padding-top:5px; padding-bottom:5px;"><![endif]-->
                        <div class="col num12" style=min-width:320px;max-width:480px;display:table-cell;vertical-align:top;width:480px>
                            <div class=col_cont style=width:100%!important>
                                <!--[if (!mso)&(!IE)]><!-->
                                <div style="border-top:0 solid transparent;border-left:0 solid transparent;border-bottom:0 solid transparent;border-right:0 solid transparent;padding-top:5px;padding-bottom:5px;padding-right:0;padding-left:0">
                                    <!--<![endif]-->
                                    <a href=${checklist} style=text-decoration:none;cursor:pointer>
                                        <div align=left class=button-container style=padding-top:0;padding-right:32px;padding-bottom:24px;padding-left:32px>
                                            <!--[if mso]><table width="100%" cellpadding="0" cellspacing="0" border="0" style="border-spacing: 0; border-collapse: collapse; mso-table-lspace:0pt; mso-table-rspace:0pt;"><tr><td style="padding-top: 0px; padding-right: 32px; padding-bottom: 24px; padding-left: 32px" align="left"><v:roundrect xmlns:v="urn:schemas-microsoft-com:vml" xmlns:w="urn:schemas-microsoft-com:office:word" href="" style="height:39pt; width:124.5pt; v-text-anchor:middle;" arcsize="8%" stroke="false" fillcolor="#1d84ff"><w:anchorlock/><v:textbox inset="0,0,0,0"><center style="color:#ffffff; font-family:Arial, sans-serif; font-size:14px"><![endif]-->
                                            <div style="text-decoration:none;display:inline-block;color:#fff;background-color:#1d84ff;border-radius:4px;-webkit-border-radius:4px;-moz-border-radius:4px;width:auto;width:auto;border-top:0 dotted #fff;border-right:0 dotted #fff;border-bottom:0 dotted #fff;border-left:0 dotted #fff;padding-top:10px;padding-bottom:10px;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;text-align:center;mso-border-alt:none;word-break:keep-all"><span style=padding-left:20px;padding-right:20px;font-size:14px;display:inline-block><span style="font-size:16px;line-height:2;word-break:break-word;font-family:Nunito,Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:32px"><span data-mce-style="font-size: 14px; line-height: 28px;" style=font-size:14px;line-height:28px>Begin Review</span></span></span></div>
                                            <!--[if mso]></center></v:textbox></v:roundrect></td></tr></table><![endif]-->
                                        </div>
                                    </a>
                                    <!--[if (!mso)&(!IE)]><!-->
                                </div>
                                <!--<![endif]-->
                            </div>
                        </div>
                        <!--[if (mso)|(IE)]></td></tr></table><![endif]-->
                        <!--[if (mso)|(IE)]></td></tr></table></td></tr></table><![endif]-->
                    </div>
                </div>
            </div>
            <div style=background-color:transparent>
                <div class=block-grid style="min-width:320px;max-width:480px;overflow-wrap:break-word;word-wrap:break-word;word-break:break-word;Margin:0 auto;background-color:#fff">
                    <div style=border-collapse:collapse;display:table;width:100%;background-color:#fff>
                        <!--[if (mso)|(IE)]><table width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:transparent;"><tr><td align="center"><table cellpadding="0" cellspacing="0" border="0" style="width:480px"><tr class="layout-full-width" style="background-color:#ffffff"><![endif]-->
                        <!--[if (mso)|(IE)]><td align="center" width="480" style="background-color:#ffffff;width:480px; border-top: 0px solid transparent; border-left: 0px solid transparent; border-bottom: 0px solid transparent; border-right: 0px solid transparent;" valign="top"><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 0px; padding-left: 0px; padding-top:5px; padding-bottom:5px;"><![endif]-->
                        <div class="col num12" style=min-width:320px;max-width:480px;display:table-cell;vertical-align:top;width:480px>
                            <div class=col_cont style=width:100%!important>
                                <!--[if (!mso)&(!IE)]><!-->
                                <div style="border-top:0 solid transparent;border-left:0 solid transparent;border-bottom:0 solid transparent;border-right:0 solid transparent;padding-top:5px;padding-bottom:5px;padding-right:0;padding-left:0">
                                    <!--<![endif]-->
                                    <!--[if mso]><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 32px; padding-left: 32px; padding-top: 00px; padding-bottom: 32px; font-family: Arial, sans-serif"><![endif]-->
                                    <div style="color:#000;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;line-height:1.5;padding-top:00px;padding-right:32px;padding-bottom:32px;padding-left:32px">
                                        <div style="line-height:1.5;font-size:12px;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;color:#000;mso-line-height-alt:18px">
                                            <p style="font-size:12px;line-height:1.5;word-break:break-word;text-align:left;font-family:Nunito,Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:18px;margin:0"><span style=font-size:12px>Click the above button, or copy and paste the following link on your web browser: <span style=color:#1d84ff>${checklist}</span></span></p>
                                        </div>
                                    </div>
                                    <!--[if mso]></td></tr></table><![endif]-->
                                    <!--[if (!mso)&(!IE)]><!-->
                                </div>
                                <!--<![endif]-->
                            </div>
                        </div>
                        <!--[if (mso)|(IE)]></td></tr></table><![endif]-->
                        <!--[if (mso)|(IE)]></td></tr></table></td></tr></table><![endif]-->
                    </div>
                </div>
            </div>
            <div style=background-color:transparent>
                <div class=block-grid style="min-width:320px;max-width:480px;overflow-wrap:break-word;word-wrap:break-word;word-break:break-word;Margin:0 auto;background-color:#fff">
                    <div style=border-collapse:collapse;display:table;width:100%;background-color:#fff>
                        <!--[if (mso)|(IE)]><table width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:transparent;"><tr><td align="center"><table cellpadding="0" cellspacing="0" border="0" style="width:480px"><tr class="layout-full-width" style="background-color:#ffffff"><![endif]-->
                        <!--[if (mso)|(IE)]><td align="center" width="480" style="background-color:#ffffff;width:480px; border-top: 0px solid transparent; border-left: 0px solid transparent; border-bottom: 0px solid transparent; border-right: 0px solid transparent;" valign="top"><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 0px; padding-left: 0px; padding-top:5px; padding-bottom:5px;"><![endif]-->
                        <div class="col num12" style=min-width:320px;max-width:480px;display:table-cell;vertical-align:top;width:480px>
                            <div class=col_cont style=width:100%!important>
                                <!--[if (!mso)&(!IE)]><!-->
                                <div style="border-top:0 solid transparent;border-left:0 solid transparent;border-bottom:0 solid transparent;border-right:0 solid transparent;padding-top:5px;padding-bottom:5px;padding-right:0;padding-left:0">
                                    <!--<![endif]-->
                                    <!--[if mso]><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 32px; padding-left: 32px; padding-top: 20px; padding-bottom: 60px; font-family: Arial, sans-serif"><![endif]-->
                                    <div style="color:#000;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;line-height:1.2;padding-top:20px;padding-right:32px;padding-bottom:60px;padding-left:32px">
                                        <div style="line-height:1.2;font-size:12px;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;color:#000;mso-line-height-alt:14px">
                                            <p style="font-size:14px;line-height:1.2;word-break:break-word;font-family:Nunito,Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:17px;margin:0"><span style=font-size:14px>Warm Regards,</span></p>
                                            <p style="font-size:14px;line-height:1.2;word-break:break-word;font-family:Nunito,Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:17px;margin:0"><span style=font-size:14px>CLEEN</span></p>
                                        </div>
                                    </div>
                                    <!--[if mso]></td></tr></table><![endif]-->
                                    <!--[if (!mso)&(!IE)]><!-->
                                </div>
                                <!--<![endif]-->
                            </div>
                        </div>
                        <!--[if (mso)|(IE)]></td></tr></table><![endif]-->
                        <!--[if (mso)|(IE)]></td></tr></table></td></tr></table><![endif]-->
                    </div>
                </div>
            </div>
            <div style=background-color:#f4f4f4>
                <div class=block-grid style="min-width:320px;max-width:480px;overflow-wrap:break-word;word-wrap:break-word;word-break:break-word;Margin:0 auto;background-color:#f4f4f4">
                    <div style=border-collapse:collapse;display:table;width:100%;background-color:#f4f4f4>
                        <!--[if (mso)|(IE)]><table width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f4f4f4;"><tr><td align="center"><table cellpadding="0" cellspacing="0" border="0" style="width:480px"><tr class="layout-full-width" style="background-color:#f4f4f4"><![endif]-->
                        <!--[if (mso)|(IE)]><td align="center" width="480" style="background-color:#f4f4f4;width:480px; border-top: 0px solid transparent; border-left: 0px solid transparent; border-bottom: 0px solid transparent; border-right: 0px solid transparent;" valign="top"><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 0px; padding-left: 0px; padding-top:5px; padding-bottom:5px;"><![endif]-->
                        <div class="col num12" style=min-width:320px;max-width:480px;display:table-cell;vertical-align:top;width:480px>
                            <div class=col_cont style=width:100%!important>
                                <!--[if (!mso)&(!IE)]><!-->
                                <div style="border-top:0 solid transparent;border-left:0 solid transparent;border-bottom:0 solid transparent;border-right:0 solid transparent;padding-top:5px;padding-bottom:5px;padding-right:0;padding-left:0">
                                    <!--<![endif]-->
                                    <!--[if mso]><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 32px; padding-left: 32px; padding-top: 20px; padding-bottom: 20px; font-family: Arial, sans-serif"><![endif]-->
                                    <div style="color:#000;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;line-height:1.2;padding-top:20px;padding-right:32px;padding-bottom:20px;padding-left:32px">
                                        <div style="line-height:1.2;font-size:12px;color:#000;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:14px">
                                            <p style=line-height:1.2;word-break:break-word;text-align:center;mso-line-height-alt:14px;margin:0><span style=color:#000>To log in to CLEEN, open this link in a web browser (e.g. Google Chrome) - <span style=color:#1d84ff>${streemlogin}</span></span></p>
                                        </div>
                                    </div>
                                    <!--[if mso]></td></tr></table><![endif]-->
                                    <!--[if (!mso)&(!IE)]><!-->
                                </div>
                                <!--<![endif]-->
                            </div>
                        </div>
                        <!--[if (mso)|(IE)]></td></tr></table><![endif]-->
                        <!--[if (mso)|(IE)]></td></tr></table></td></tr></table><![endif]-->
                    </div>
                </div>
            </div>
            <!--[if (mso)|(IE)]></td></tr></table><![endif]-->
        </td>
    </tr>
    </tbody>
</table>
<!--[if (IE)]></div><![endif]-->
</body>
</html>');

--changeset sathyam:02-seed-data-11
INSERT INTO email_templates(id, name, content)
VALUES (8, 'PROTOTYPE_SIGNING_REQUEST', '
<!doctype html>
<html xmlns=http://www.w3.org/1999/xhtml xmlns:o=urn:schemas-microsoft-com:office:office xmlns:v=urn:schemas-microsoft-com:vml>
<head>
    <!--[if gte mso 9]><xml><o:OfficeDocumentSettings><o:AllowPNG/><o:PixelsPerInch>96</o:PixelsPerInch></o:OfficeDocumentSettings></xml><![endif]-->
    <meta content="text/html; charset=utf-8" http-equiv=Content-Type>
    <meta content="width=device-width" name=viewport>
    <!--[if !mso]><!-->
    <meta content="IE=edge" http-equiv=X-UA-Compatible>
    <!--<![endif]-->
    <title></title>
    <!--[if !mso]><!-->
    <link href="https://fonts.googleapis.com/css?family=Nunito" rel=stylesheet>
    <!--<![endif]-->
    <style>body{margin:0;padding:0}table,td,tr{vertical-align:top;border-collapse:collapse}*{line-height:inherit}a[x-apple-data-detectors=true]{color:inherit!important;text-decoration:none!important}</style>
    <style id=media-query>@media (max-width:500px){.block-grid,.col{min-width:320px!important;max-width:100%!important;display:block!important}.block-grid{width:100%!important}.col{width:100%!important}.col_cont{margin:0 auto}img.fullwidth,img.fullwidthOnMobile{max-width:100%!important}.no-stack .col{min-width:0!important;display:table-cell!important}.no-stack.two-up .col{width:50%!important}.no-stack .col.num2{width:16.6%!important}.no-stack .col.num3{width:25%!important}.no-stack .col.num4{width:33%!important}.no-stack .col.num5{width:41.6%!important}.no-stack .col.num6{width:50%!important}.no-stack .col.num7{width:58.3%!important}.no-stack .col.num8{width:66.6%!important}.no-stack .col.num9{width:75%!important}.no-stack .col.num10{width:83.3%!important}.video-block{max-width:none!important}.mobile_hide{min-height:0;max-height:0;max-width:0;display:none;overflow:hidden;font-size:0}.desktop_hide{display:block!important;max-height:none!important}}</style>
</head>
<body class=clean-body style=margin:0;padding:0;-webkit-text-size-adjust:100%;background-color:#f4f4f4>
<!--[if IE]><div class="ie-browser"><![endif]-->
<table bgcolor=#f4f4f4 cellpadding=0 cellspacing=0 class=nl-container role=presentation style=table-layout:fixed;vertical-align:top;min-width:320px;border-spacing:0;border-collapse:collapse;mso-table-lspace:0;mso-table-rspace:0;background-color:#f4f4f4;width:100% valign=top width=100%>
    <tbody>
    <tr style=vertical-align:top valign=top>
        <td style=word-break:break-word;vertical-align:top valign=top>
            <!--[if (mso)|(IE)]><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td align="center" style="background-color:#f4f4f4"><![endif]-->
            <div style=background-color:#f4f4f4>
                <div class=block-grid style="min-width:320px;max-width:480px;overflow-wrap:break-word;word-wrap:break-word;word-break:break-word;Margin:0 auto;background-color:#f4f4f4">
                    <div style=border-collapse:collapse;display:table;width:100%;background-color:#f4f4f4>
                        <!--[if (mso)|(IE)]><table width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f4f4f4;"><tr><td align="center"><table cellpadding="0" cellspacing="0" border="0" style="width:480px"><tr class="layout-full-width" style="background-color:#f4f4f4"><![endif]-->
                        <!--[if (mso)|(IE)]><td align="center" width="480" style="background-color:#f4f4f4;width:480px; border-top: 0px solid transparent; border-left: 0px solid transparent; border-bottom: 0px solid transparent; border-right: 0px solid transparent;" valign="top"><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 0px; padding-left: 0px; padding-top:50px; padding-bottom:0px;"><![endif]-->
                        <div class="col num12" style=min-width:320px;max-width:480px;display:table-cell;vertical-align:top;width:480px>
                            <div class=col_cont style=width:100%!important>
                                <!--[if (!mso)&(!IE)]><!-->
                                <div style="border-top:0 solid transparent;border-left:0 solid transparent;border-bottom:0 solid transparent;border-right:0 solid transparent;padding-top:50px;padding-bottom:0;padding-right:0;padding-left:0">
                                    <!--<![endif]-->
                                    <div></div>
                                    <!--[if (!mso)&(!IE)]><!-->
                                </div>
                                <!--<![endif]-->
                            </div>
                        </div>
                        <!--[if (mso)|(IE)]></td></tr></table><![endif]-->
                        <!--[if (mso)|(IE)]></td></tr></table></td></tr></table><![endif]-->
                    </div>
                </div>
            </div>
            <div style=background-color:#f4f4f4>
                <div class=block-grid style="min-width:320px;max-width:480px;overflow-wrap:break-word;word-wrap:break-word;word-break:break-word;Margin:0 auto;background-color:#fff">
                    <div style=border-collapse:collapse;display:table;width:100%;background-color:#fff>
                        <!--[if (mso)|(IE)]><table width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f4f4f4;"><tr><td align="center"><table cellpadding="0" cellspacing="0" border="0" style="width:480px"><tr class="layout-full-width" style="background-color:#ffffff"><![endif]-->
                        <!--[if (mso)|(IE)]><td align="center" width="480" style="background-color:#ffffff;width:480px; border-top: 0px solid transparent; border-left: 0px solid transparent; border-bottom: 0px solid transparent; border-right: 0px solid transparent;" valign="top"><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 0px; padding-left: 0px; padding-top:32px; padding-bottom:32px;"><![endif]-->
                        <div class="col num12" style=min-width:320px;max-width:480px;display:table-cell;vertical-align:top;width:480px>
                            <div class=col_cont style=width:100%!important>
                                <!--[if (!mso)&(!IE)]><!-->
                                <div style="border-top:0 solid transparent;border-left:0 solid transparent;border-bottom:0 solid transparent;border-right:0 solid transparent;padding-top:32px;padding-bottom:32px;padding-right:0;padding-left:0">
                                    <!--<![endif]-->
                                    <div align=center class="img-container center fixedwidth" style=padding-right:0;padding-left:0>
                                        <!--[if mso]><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr style="line-height:0px"><td style="padding-right: 0px;padding-left: 0px;" align="center"><![endif]--><img align=center alt="Alternate text" border=0 class="center fixedwidth" src=cid:streem-color style=text-decoration:none;-ms-interpolation-mode:bicubic;height:auto;border:0;width:100%;max-width:144px;display:block title="Alternate text" width=144>
                                        <!--[if mso]></td></tr></table><![endif]-->
                                    </div>
                                    <!--[if (!mso)&(!IE)]><!-->
                                </div>
                                <!--<![endif]-->
                            </div>
                        </div>
                        <!--[if (mso)|(IE)]></td></tr></table><![endif]-->
                        <!--[if (mso)|(IE)]></td></tr></table></td></tr></table><![endif]-->
                    </div>
                </div>
            </div>
            <div style=background-color:#f4f4f4>
                <div class=block-grid style="min-width:320px;max-width:480px;overflow-wrap:break-word;word-wrap:break-word;word-break:break-word;Margin:0 auto;background-color:#fff">
                    <div style=border-collapse:collapse;display:table;width:100%;background-color:#fff>
                        <!--[if (mso)|(IE)]><table width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f4f4f4;"><tr><td align="center"><table cellpadding="0" cellspacing="0" border="0" style="width:480px"><tr class="layout-full-width" style="background-color:#ffffff"><![endif]-->
                        <!--[if (mso)|(IE)]><td align="center" width="480" style="background-color:#ffffff;width:480px; border-top: 0px solid transparent; border-left: 0px solid transparent; border-bottom: 0px solid transparent; border-right: 0px solid transparent;" valign="top"><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 0px; padding-left: 0px; padding-top:5px; padding-bottom:5px;"><![endif]-->
                        <div class="col num12" style=min-width:320px;max-width:480px;display:table-cell;vertical-align:top;width:480px>
                            <div class=col_cont style=width:100%!important>
                                <!--[if (!mso)&(!IE)]><!-->
                                <div style="border-top:0 solid transparent;border-left:0 solid transparent;border-bottom:0 solid transparent;border-right:0 solid transparent;padding-top:5px;padding-bottom:5px;padding-right:0;padding-left:0">
                                    <!--<![endif]-->
                                    <!--[if mso]><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 40px; padding-left: 40px; padding-top: 0px; padding-bottom: 32px; font-family: Arial, sans-serif"><![endif]-->
                                    <div style="color:#000;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;line-height:1.2;padding-top:0;padding-right:40px;padding-bottom:32px;padding-left:40px">
                                        <div style="line-height:1.2;font-size:12px;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;color:#000;mso-line-height-alt:14px">
                                            <p style="font-size:20px;line-height:1.2;word-break:break-word;text-align:center;font-family:Nunito,Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:24px;margin:0"><span style=font-size:20px>You have been requested to sign a Prototype</span></p>
                                        </div>
                                    </div>
                                    <!--[if mso]></td></tr></table><![endif]-->
                                    <table border=0 cellpadding=0 cellspacing=0 class=divider role=presentation style=table-layout:fixed;vertical-align:top;border-spacing:0;border-collapse:collapse;mso-table-lspace:0;mso-table-rspace:0;min-width:100%;-ms-text-size-adjust:100%;-webkit-text-size-adjust:100% valign=top width=100%>
                                        <tbody>
                                        <tr style=vertical-align:top valign=top>
                                            <td class=divider_inner style=word-break:break-word;vertical-align:top;min-width:100%;-ms-text-size-adjust:100%;-webkit-text-size-adjust:100%;padding-top:0;padding-right:10px;padding-bottom:0;padding-left:10px valign=top>
                                                <table align=center border=0 cellpadding=0 cellspacing=0 class=divider_content role=presentation style="table-layout:fixed;vertical-align:top;border-spacing:0;border-collapse:collapse;mso-table-lspace:0;mso-table-rspace:0;border-top:1px solid #eee;width:100%" valign=top width=100%>
                                                    <tbody>
                                                    <tr style=vertical-align:top valign=top>
                                                        <td style=word-break:break-word;vertical-align:top;-ms-text-size-adjust:100%;-webkit-text-size-adjust:100% valign=top><span></span></td>
                                                    </tr>
                                                    </tbody>
                                                </table>
                                            </td>
                                        </tr>
                                        </tbody>
                                    </table>
                                    <!--[if (!mso)&(!IE)]><!-->
                                </div>
                                <!--<![endif]-->
                            </div>
                        </div>
                        <!--[if (mso)|(IE)]></td></tr></table><![endif]-->
                        <!--[if (mso)|(IE)]></td></tr></table></td></tr></table><![endif]-->
                    </div>
                </div>
            </div>
            <div style=background-color:transparent>
                <div class=block-grid style="min-width:320px;max-width:480px;overflow-wrap:break-word;word-wrap:break-word;word-break:break-word;Margin:0 auto;background-color:#fff">
                    <div style=border-collapse:collapse;display:table;width:100%;background-color:#fff>
                        <!--[if (mso)|(IE)]><table width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:transparent;"><tr><td align="center"><table cellpadding="0" cellspacing="0" border="0" style="width:480px"><tr class="layout-full-width" style="background-color:#ffffff"><![endif]-->
                        <!--[if (mso)|(IE)]><td align="center" width="480" style="background-color:#ffffff;width:480px; border-top: 0px solid transparent; border-left: 0px solid transparent; border-bottom: 0px solid transparent; border-right: 0px solid transparent;" valign="top"><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 0px; padding-left: 0px; padding-top:5px; padding-bottom:5px;"><![endif]-->
                        <div class="col num12" style=min-width:320px;max-width:480px;display:table-cell;vertical-align:top;width:480px>
                            <div class=col_cont style=width:100%!important>
                                <!--[if (!mso)&(!IE)]><!-->
                                <div style="border-top:0 solid transparent;border-left:0 solid transparent;border-bottom:0 solid transparent;border-right:0 solid transparent;padding-top:5px;padding-bottom:5px;padding-right:0;padding-left:0">
                                    <!--<![endif]-->
                                    <!--[if mso]><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 32px; padding-left: 32px; padding-top: 32px; padding-bottom: 24px; font-family: Arial, sans-serif"><![endif]-->
                                    <div style="color:#000;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;line-height:1.5;padding-top:32px;padding-right:32px;padding-bottom:24px;padding-left:32px">
                                        <div style="line-height:1.5;font-size:12px;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;color:#000;mso-line-height-alt:18px">
                                            <p style="font-size:14px;line-height:1.5;word-break:break-word;font-family:Nunito,Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:21px;margin:0">Hello,</p>
                                            <p style="font-size:14px;line-height:1.5;word-break:break-word;font-family:Nunito,Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:21px;margin:0"> </p>
                                            <p style="font-size:14px;line-height:1.5;word-break:break-word;font-family:Nunito,Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:21px;margin:0">You are requested to sign a prototype, and make it ready for release. Please click the button below to sign the finalised prototype.</p>
                                        </div>
                                    </div>
                                    <!--[if mso]></td></tr></table><![endif]-->
                                    <!--[if (!mso)&(!IE)]><!-->
                                </div>
                                <!--<![endif]-->
                            </div>
                        </div>
                        <!--[if (mso)|(IE)]></td></tr></table><![endif]-->
                        <!--[if (mso)|(IE)]></td></tr></table></td></tr></table><![endif]-->
                    </div>
                </div>
            </div>
            <div style=background-color:transparent>
                <div class=block-grid style="min-width:320px;max-width:480px;overflow-wrap:break-word;word-wrap:break-word;word-break:break-word;Margin:0 auto;background-color:#fff">
                    <div style=border-collapse:collapse;display:table;width:100%;background-color:#fff>
                        <!--[if (mso)|(IE)]><table width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:transparent;"><tr><td align="center"><table cellpadding="0" cellspacing="0" border="0" style="width:480px"><tr class="layout-full-width" style="background-color:#ffffff"><![endif]-->
                        <!--[if (mso)|(IE)]><td align="center" width="480" style="background-color:#ffffff;width:480px; border-top: 0px solid transparent; border-left: 0px solid transparent; border-bottom: 0px solid transparent; border-right: 0px solid transparent;" valign="top"><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 0px; padding-left: 0px; padding-top:5px; padding-bottom:5px;"><![endif]-->
                        <div class="col num12" style=min-width:320px;max-width:480px;display:table-cell;vertical-align:top;width:480px>
                            <div class=col_cont style=width:100%!important>
                                <!--[if (!mso)&(!IE)]><!-->
                                <div style="border-top:0 solid transparent;border-left:0 solid transparent;border-bottom:0 solid transparent;border-right:0 solid transparent;padding-top:5px;padding-bottom:5px;padding-right:0;padding-left:0">
                                    <!--<![endif]-->
                                    <a href=${checklist} style=text-decoration:none;cursor:pointer>
                                        <div align=left class=button-container style=padding-top:0;padding-right:32px;padding-bottom:24px;padding-left:32px>
                                            <!--[if mso]><table width="100%" cellpadding="0" cellspacing="0" border="0" style="border-spacing: 0; border-collapse: collapse; mso-table-lspace:0pt; mso-table-rspace:0pt;"><tr><td style="padding-top: 0px; padding-right: 32px; padding-bottom: 24px; padding-left: 32px" align="left"><v:roundrect xmlns:v="urn:schemas-microsoft-com:vml" xmlns:w="urn:schemas-microsoft-com:office:word" href="" style="height:39pt; width:129.75pt; v-text-anchor:middle;" arcsize="8%" stroke="false" fillcolor="#1d84ff"><w:anchorlock/><v:textbox inset="0,0,0,0"><center style="color:#ffffff; font-family:Arial, sans-serif; font-size:14px"><![endif]-->
                                            <div style="text-decoration:none;display:inline-block;color:#fff;background-color:#1d84ff;border-radius:4px;-webkit-border-radius:4px;-moz-border-radius:4px;width:auto;width:auto;border-top:0 dotted #fff;border-right:0 dotted #fff;border-bottom:0 dotted #fff;border-left:0 dotted #fff;padding-top:10px;padding-bottom:10px;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;text-align:center;mso-border-alt:none;word-break:keep-all"><span style=padding-left:20px;padding-right:20px;font-size:14px;display:inline-block><span style="font-size:16px;line-height:2;word-break:break-word;font-family:Nunito,Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:32px"><span data-mce-style="font-size: 14px; line-height: 28px;" style=font-size:14px;line-height:28px>Sign Prototype</span></span></span></div>
                                            <!--[if mso]></center></v:textbox></v:roundrect></td></tr></table><![endif]-->
                                        </div>
                                    </a>
                                    <!--[if (!mso)&(!IE)]><!-->
                                </div>
                                <!--<![endif]-->
                            </div>
                        </div>
                        <!--[if (mso)|(IE)]></td></tr></table><![endif]-->
                        <!--[if (mso)|(IE)]></td></tr></table></td></tr></table><![endif]-->
                    </div>
                </div>
            </div>
            <div style=background-color:transparent>
                <div class=block-grid style="min-width:320px;max-width:480px;overflow-wrap:break-word;word-wrap:break-word;word-break:break-word;Margin:0 auto;background-color:#fff">
                    <div style=border-collapse:collapse;display:table;width:100%;background-color:#fff>
                        <!--[if (mso)|(IE)]><table width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:transparent;"><tr><td align="center"><table cellpadding="0" cellspacing="0" border="0" style="width:480px"><tr class="layout-full-width" style="background-color:#ffffff"><![endif]-->
                        <!--[if (mso)|(IE)]><td align="center" width="480" style="background-color:#ffffff;width:480px; border-top: 0px solid transparent; border-left: 0px solid transparent; border-bottom: 0px solid transparent; border-right: 0px solid transparent;" valign="top"><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 0px; padding-left: 0px; padding-top:5px; padding-bottom:5px;"><![endif]-->
                        <div class="col num12" style=min-width:320px;max-width:480px;display:table-cell;vertical-align:top;width:480px>
                            <div class=col_cont style=width:100%!important>
                                <!--[if (!mso)&(!IE)]><!-->
                                <div style="border-top:0 solid transparent;border-left:0 solid transparent;border-bottom:0 solid transparent;border-right:0 solid transparent;padding-top:5px;padding-bottom:5px;padding-right:0;padding-left:0">
                                    <!--<![endif]-->
                                    <!--[if mso]><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 32px; padding-left: 32px; padding-top: 00px; padding-bottom: 32px; font-family: Arial, sans-serif"><![endif]-->
                                    <div style="color:#000;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;line-height:1.5;padding-top:00px;padding-right:32px;padding-bottom:32px;padding-left:32px">
                                        <div style="line-height:1.5;font-size:12px;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;color:#000;mso-line-height-alt:18px">
                                            <p style="font-size:12px;line-height:1.5;word-break:break-word;text-align:left;font-family:Nunito,Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:18px;margin:0"><span style=font-size:12px>Click the above button, or copy and paste the following link on your web browser: <span style=color:#1d84ff>${checklist}</span></span></p>
                                        </div>
                                    </div>
                                    <!--[if mso]></td></tr></table><![endif]-->
                                    <!--[if (!mso)&(!IE)]><!-->
                                </div>
                                <!--<![endif]-->
                            </div>
                        </div>
                        <!--[if (mso)|(IE)]></td></tr></table><![endif]-->
                        <!--[if (mso)|(IE)]></td></tr></table></td></tr></table><![endif]-->
                    </div>
                </div>
            </div>
            <div style=background-color:transparent>
                <div class=block-grid style="min-width:320px;max-width:480px;overflow-wrap:break-word;word-wrap:break-word;word-break:break-word;Margin:0 auto;background-color:#fff">
                    <div style=border-collapse:collapse;display:table;width:100%;background-color:#fff>
                        <!--[if (mso)|(IE)]><table width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:transparent;"><tr><td align="center"><table cellpadding="0" cellspacing="0" border="0" style="width:480px"><tr class="layout-full-width" style="background-color:#ffffff"><![endif]-->
                        <!--[if (mso)|(IE)]><td align="center" width="480" style="background-color:#ffffff;width:480px; border-top: 0px solid transparent; border-left: 0px solid transparent; border-bottom: 0px solid transparent; border-right: 0px solid transparent;" valign="top"><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 0px; padding-left: 0px; padding-top:5px; padding-bottom:5px;"><![endif]-->
                        <div class="col num12" style=min-width:320px;max-width:480px;display:table-cell;vertical-align:top;width:480px>
                            <div class=col_cont style=width:100%!important>
                                <!--[if (!mso)&(!IE)]><!-->
                                <div style="border-top:0 solid transparent;border-left:0 solid transparent;border-bottom:0 solid transparent;border-right:0 solid transparent;padding-top:5px;padding-bottom:5px;padding-right:0;padding-left:0">
                                    <!--<![endif]-->
                                    <!--[if mso]><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 32px; padding-left: 32px; padding-top: 20px; padding-bottom: 60px; font-family: Arial, sans-serif"><![endif]-->
                                    <div style="color:#000;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;line-height:1.2;padding-top:20px;padding-right:32px;padding-bottom:60px;padding-left:32px">
                                        <div style="line-height:1.2;font-size:12px;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;color:#000;mso-line-height-alt:14px">
                                            <p style="font-size:14px;line-height:1.2;word-break:break-word;font-family:Nunito,Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:17px;margin:0"><span style=font-size:14px>Warm Regards,</span></p>
                                            <p style="font-size:14px;line-height:1.2;word-break:break-word;font-family:Nunito,Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:17px;margin:0"><span style=font-size:14px>CLEEN</span></p>
                                        </div>
                                    </div>
                                    <!--[if mso]></td></tr></table><![endif]-->
                                    <!--[if (!mso)&(!IE)]><!-->
                                </div>
                                <!--<![endif]-->
                            </div>
                        </div>
                        <!--[if (mso)|(IE)]></td></tr></table><![endif]-->
                        <!--[if (mso)|(IE)]></td></tr></table></td></tr></table><![endif]-->
                    </div>
                </div>
            </div>
            <div style=background-color:#f4f4f4>
                <div class=block-grid style="min-width:320px;max-width:480px;overflow-wrap:break-word;word-wrap:break-word;word-break:break-word;Margin:0 auto;background-color:#f4f4f4">
                    <div style=border-collapse:collapse;display:table;width:100%;background-color:#f4f4f4>
                        <!--[if (mso)|(IE)]><table width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f4f4f4;"><tr><td align="center"><table cellpadding="0" cellspacing="0" border="0" style="width:480px"><tr class="layout-full-width" style="background-color:#f4f4f4"><![endif]-->
                        <!--[if (mso)|(IE)]><td align="center" width="480" style="background-color:#f4f4f4;width:480px; border-top: 0px solid transparent; border-left: 0px solid transparent; border-bottom: 0px solid transparent; border-right: 0px solid transparent;" valign="top"><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 0px; padding-left: 0px; padding-top:5px; padding-bottom:5px;"><![endif]-->
                        <div class="col num12" style=min-width:320px;max-width:480px;display:table-cell;vertical-align:top;width:480px>
                            <div class=col_cont style=width:100%!important>
                                <!--[if (!mso)&(!IE)]><!-->
                                <div style="border-top:0 solid transparent;border-left:0 solid transparent;border-bottom:0 solid transparent;border-right:0 solid transparent;padding-top:5px;padding-bottom:5px;padding-right:0;padding-left:0">
                                    <!--<![endif]-->
                                    <!--[if mso]><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 32px; padding-left: 32px; padding-top: 20px; padding-bottom: 20px; font-family: Arial, sans-serif"><![endif]-->
                                    <div style="color:#000;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;line-height:1.2;padding-top:20px;padding-right:32px;padding-bottom:20px;padding-left:32px">
                                        <div style="line-height:1.2;font-size:12px;color:#000;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:14px">
                                            <p style=line-height:1.2;word-break:break-word;text-align:center;mso-line-height-alt:14px;margin:0><span style=color:#000>To log in to CLEEN, open this link in a web browser (e.g. Google Chrome) - <span style=color:#1d84ff>${streemlogin}</span></span></p>
                                        </div>
                                    </div>
                                    <!--[if mso]></td></tr></table><![endif]-->
                                    <!--[if (!mso)&(!IE)]><!-->
                                </div>
                                <!--<![endif]-->
                            </div>
                        </div>
                        <!--[if (mso)|(IE)]></td></tr></table><![endif]-->
                        <!--[if (mso)|(IE)]></td></tr></table></td></tr></table><![endif]-->
                    </div>
                </div>
            </div>
            <!--[if (mso)|(IE)]></td></tr></table><![endif]-->
        </td>
    </tr>
    </tbody>
</table>
<!--[if (IE)]></div><![endif]-->
</body>
</html>');

--changeset sathyam:02-seed-data-12
INSERT INTO email_templates(id, name, content)
VALUES (9, 'PROTOTYPE_READY_FOR_SIGNING', '
<!doctype html>
<html xmlns=http://www.w3.org/1999/xhtml xmlns:o=urn:schemas-microsoft-com:office:office xmlns:v=urn:schemas-microsoft-com:vml>
<head>
    <!--[if gte mso 9]><xml><o:OfficeDocumentSettings><o:AllowPNG/><o:PixelsPerInch>96</o:PixelsPerInch></o:OfficeDocumentSettings></xml><![endif]-->
    <meta content="text/html; charset=utf-8" http-equiv=Content-Type>
    <meta content="width=device-width" name=viewport>
    <!--[if !mso]><!-->
    <meta content="IE=edge" http-equiv=X-UA-Compatible>
    <!--<![endif]-->
    <title></title>
    <!--[if !mso]><!-->
    <link href="https://fonts.googleapis.com/css?family=Nunito" rel=stylesheet>
    <!--<![endif]-->
    <style>body{margin:0;padding:0}table,td,tr{vertical-align:top;border-collapse:collapse}*{line-height:inherit}a[x-apple-data-detectors=true]{color:inherit!important;text-decoration:none!important}</style>
    <style id=media-query>@media (max-width:500px){.block-grid,.col{min-width:320px!important;max-width:100%!important;display:block!important}.block-grid{width:100%!important}.col{width:100%!important}.col_cont{margin:0 auto}img.fullwidth,img.fullwidthOnMobile{max-width:100%!important}.no-stack .col{min-width:0!important;display:table-cell!important}.no-stack.two-up .col{width:50%!important}.no-stack .col.num2{width:16.6%!important}.no-stack .col.num3{width:25%!important}.no-stack .col.num4{width:33%!important}.no-stack .col.num5{width:41.6%!important}.no-stack .col.num6{width:50%!important}.no-stack .col.num7{width:58.3%!important}.no-stack .col.num8{width:66.6%!important}.no-stack .col.num9{width:75%!important}.no-stack .col.num10{width:83.3%!important}.video-block{max-width:none!important}.mobile_hide{min-height:0;max-height:0;max-width:0;display:none;overflow:hidden;font-size:0}.desktop_hide{display:block!important;max-height:none!important}}</style>
</head>
<body class=clean-body style=margin:0;padding:0;-webkit-text-size-adjust:100%;background-color:#f4f4f4>
<!--[if IE]><div class="ie-browser"><![endif]-->
<table bgcolor=#f4f4f4 cellpadding=0 cellspacing=0 class=nl-container role=presentation style=table-layout:fixed;vertical-align:top;min-width:320px;border-spacing:0;border-collapse:collapse;mso-table-lspace:0;mso-table-rspace:0;background-color:#f4f4f4;width:100% valign=top width=100%>
    <tbody>
    <tr style=vertical-align:top valign=top>
        <td style=word-break:break-word;vertical-align:top valign=top>
            <!--[if (mso)|(IE)]><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td align="center" style="background-color:#f4f4f4"><![endif]-->
            <div style=background-color:#f4f4f4>
                <div class=block-grid style="min-width:320px;max-width:480px;overflow-wrap:break-word;word-wrap:break-word;word-break:break-word;Margin:0 auto;background-color:#f4f4f4">
                    <div style=border-collapse:collapse;display:table;width:100%;background-color:#f4f4f4>
                        <!--[if (mso)|(IE)]><table width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f4f4f4;"><tr><td align="center"><table cellpadding="0" cellspacing="0" border="0" style="width:480px"><tr class="layout-full-width" style="background-color:#f4f4f4"><![endif]-->
                        <!--[if (mso)|(IE)]><td align="center" width="480" style="background-color:#f4f4f4;width:480px; border-top: 0px solid transparent; border-left: 0px solid transparent; border-bottom: 0px solid transparent; border-right: 0px solid transparent;" valign="top"><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 0px; padding-left: 0px; padding-top:50px; padding-bottom:0px;"><![endif]-->
                        <div class="col num12" style=min-width:320px;max-width:480px;display:table-cell;vertical-align:top;width:480px>
                            <div class=col_cont style=width:100%!important>
                                <!--[if (!mso)&(!IE)]><!-->
                                <div style="border-top:0 solid transparent;border-left:0 solid transparent;border-bottom:0 solid transparent;border-right:0 solid transparent;padding-top:50px;padding-bottom:0;padding-right:0;padding-left:0">
                                    <!--<![endif]-->
                                    <div></div>
                                    <!--[if (!mso)&(!IE)]><!-->
                                </div>
                                <!--<![endif]-->
                            </div>
                        </div>
                        <!--[if (mso)|(IE)]></td></tr></table><![endif]-->
                        <!--[if (mso)|(IE)]></td></tr></table></td></tr></table><![endif]-->
                    </div>
                </div>
            </div>
            <div style=background-color:#f4f4f4>
                <div class=block-grid style="min-width:320px;max-width:480px;overflow-wrap:break-word;word-wrap:break-word;word-break:break-word;Margin:0 auto;background-color:#fff">
                    <div style=border-collapse:collapse;display:table;width:100%;background-color:#fff>
                        <!--[if (mso)|(IE)]><table width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f4f4f4;"><tr><td align="center"><table cellpadding="0" cellspacing="0" border="0" style="width:480px"><tr class="layout-full-width" style="background-color:#ffffff"><![endif]-->
                        <!--[if (mso)|(IE)]><td align="center" width="480" style="background-color:#ffffff;width:480px; border-top: 0px solid transparent; border-left: 0px solid transparent; border-bottom: 0px solid transparent; border-right: 0px solid transparent;" valign="top"><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 0px; padding-left: 0px; padding-top:32px; padding-bottom:32px;"><![endif]-->
                        <div class="col num12" style=min-width:320px;max-width:480px;display:table-cell;vertical-align:top;width:480px>
                            <div class=col_cont style=width:100%!important>
                                <!--[if (!mso)&(!IE)]><!-->
                                <div style="border-top:0 solid transparent;border-left:0 solid transparent;border-bottom:0 solid transparent;border-right:0 solid transparent;padding-top:32px;padding-bottom:32px;padding-right:0;padding-left:0">
                                    <!--<![endif]-->
                                    <div align=center class="img-container center fixedwidth" style=padding-right:0;padding-left:0>
                                        <!--[if mso]><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr style="line-height:0px"><td style="padding-right: 0px;padding-left: 0px;" align="center"><![endif]--><img align=center alt="Alternate text" border=0 class="center fixedwidth" src=cid:streem-color style=text-decoration:none;-ms-interpolation-mode:bicubic;height:auto;border:0;width:100%;max-width:144px;display:block title="Alternate text" width=144>
                                        <!--[if mso]></td></tr></table><![endif]-->
                                    </div>
                                    <!--[if (!mso)&(!IE)]><!-->
                                </div>
                                <!--<![endif]-->
                            </div>
                        </div>
                        <!--[if (mso)|(IE)]></td></tr></table><![endif]-->
                        <!--[if (mso)|(IE)]></td></tr></table></td></tr></table><![endif]-->
                    </div>
                </div>
            </div>
            <div style=background-color:#f4f4f4>
                <div class=block-grid style="min-width:320px;max-width:480px;overflow-wrap:break-word;word-wrap:break-word;word-break:break-word;Margin:0 auto;background-color:#fff">
                    <div style=border-collapse:collapse;display:table;width:100%;background-color:#fff>
                        <!--[if (mso)|(IE)]><table width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f4f4f4;"><tr><td align="center"><table cellpadding="0" cellspacing="0" border="0" style="width:480px"><tr class="layout-full-width" style="background-color:#ffffff"><![endif]-->
                        <!--[if (mso)|(IE)]><td align="center" width="480" style="background-color:#ffffff;width:480px; border-top: 0px solid transparent; border-left: 0px solid transparent; border-bottom: 0px solid transparent; border-right: 0px solid transparent;" valign="top"><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 0px; padding-left: 0px; padding-top:5px; padding-bottom:5px;"><![endif]-->
                        <div class="col num12" style=min-width:320px;max-width:480px;display:table-cell;vertical-align:top;width:480px>
                            <div class=col_cont style=width:100%!important>
                                <!--[if (!mso)&(!IE)]><!-->
                                <div style="border-top:0 solid transparent;border-left:0 solid transparent;border-bottom:0 solid transparent;border-right:0 solid transparent;padding-top:5px;padding-bottom:5px;padding-right:0;padding-left:0">
                                    <!--<![endif]-->
                                    <!--[if mso]><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 40px; padding-left: 40px; padding-top: 0px; padding-bottom: 32px; font-family: Arial, sans-serif"><![endif]-->
                                    <div style="color:#000;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;line-height:1.2;padding-top:0;padding-right:40px;padding-bottom:32px;padding-left:40px">
                                        <div style="line-height:1.2;font-size:12px;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;color:#000;mso-line-height-alt:14px">
                                            <p style="font-size:20px;line-height:1.2;word-break:break-word;text-align:center;font-family:Nunito,Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:24px;margin:0"><span style=font-size:20px>A Prototype has been successfully reviewed and is ready for signing</span></p>
                                        </div>
                                    </div>
                                    <!--[if mso]></td></tr></table><![endif]-->
                                    <table border=0 cellpadding=0 cellspacing=0 class=divider role=presentation style=table-layout:fixed;vertical-align:top;border-spacing:0;border-collapse:collapse;mso-table-lspace:0;mso-table-rspace:0;min-width:100%;-ms-text-size-adjust:100%;-webkit-text-size-adjust:100% valign=top width=100%>
                                        <tbody>
                                        <tr style=vertical-align:top valign=top>
                                            <td class=divider_inner style=word-break:break-word;vertical-align:top;min-width:100%;-ms-text-size-adjust:100%;-webkit-text-size-adjust:100%;padding-top:0;padding-right:10px;padding-bottom:0;padding-left:10px valign=top>
                                                <table align=center border=0 cellpadding=0 cellspacing=0 class=divider_content role=presentation style="table-layout:fixed;vertical-align:top;border-spacing:0;border-collapse:collapse;mso-table-lspace:0;mso-table-rspace:0;border-top:1px solid #eee;width:100%" valign=top width=100%>
                                                    <tbody>
                                                    <tr style=vertical-align:top valign=top>
                                                        <td style=word-break:break-word;vertical-align:top;-ms-text-size-adjust:100%;-webkit-text-size-adjust:100% valign=top><span></span></td>
                                                    </tr>
                                                    </tbody>
                                                </table>
                                            </td>
                                        </tr>
                                        </tbody>
                                    </table>
                                    <!--[if (!mso)&(!IE)]><!-->
                                </div>
                                <!--<![endif]-->
                            </div>
                        </div>
                        <!--[if (mso)|(IE)]></td></tr></table><![endif]-->
                        <!--[if (mso)|(IE)]></td></tr></table></td></tr></table><![endif]-->
                    </div>
                </div>
            </div>
            <div style=background-color:transparent>
                <div class=block-grid style="min-width:320px;max-width:480px;overflow-wrap:break-word;word-wrap:break-word;word-break:break-word;Margin:0 auto;background-color:#fff">
                    <div style=border-collapse:collapse;display:table;width:100%;background-color:#fff>
                        <!--[if (mso)|(IE)]><table width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:transparent;"><tr><td align="center"><table cellpadding="0" cellspacing="0" border="0" style="width:480px"><tr class="layout-full-width" style="background-color:#ffffff"><![endif]-->
                        <!--[if (mso)|(IE)]><td align="center" width="480" style="background-color:#ffffff;width:480px; border-top: 0px solid transparent; border-left: 0px solid transparent; border-bottom: 0px solid transparent; border-right: 0px solid transparent;" valign="top"><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 0px; padding-left: 0px; padding-top:5px; padding-bottom:5px;"><![endif]-->
                        <div class="col num12" style=min-width:320px;max-width:480px;display:table-cell;vertical-align:top;width:480px>
                            <div class=col_cont style=width:100%!important>
                                <!--[if (!mso)&(!IE)]><!-->
                                <div style="border-top:0 solid transparent;border-left:0 solid transparent;border-bottom:0 solid transparent;border-right:0 solid transparent;padding-top:5px;padding-bottom:5px;padding-right:0;padding-left:0">
                                    <!--<![endif]-->
                                    <!--[if mso]><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 32px; padding-left: 32px; padding-top: 32px; padding-bottom: 24px; font-family: Arial, sans-serif"><![endif]-->
                                    <div style="color:#000;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;line-height:1.5;padding-top:32px;padding-right:32px;padding-bottom:24px;padding-left:32px">
                                        <div style="line-height:1.5;font-size:12px;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;color:#000;mso-line-height-alt:18px">
                                            <p style="font-size:14px;line-height:1.5;word-break:break-word;font-family:Nunito,Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:21px;margin:0">Hello,</p>
                                            <p style="font-size:14px;line-height:1.5;word-break:break-word;font-family:Nunito,Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:21px;margin:0"> </p>
                                            <p style="font-size:14px;line-height:1.5;word-break:break-word;font-family:Nunito,Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:21px;margin:0">All Reviewers of this Prototype have collectively agreed with its design. You can now proceed with signing it to make it ready for release.</p>
                                        </div>
                                    </div>
                                    <!--[if mso]></td></tr></table><![endif]-->
                                    <!--[if (!mso)&(!IE)]><!-->
                                </div>
                                <!--<![endif]-->
                            </div>
                        </div>
                        <!--[if (mso)|(IE)]></td></tr></table><![endif]-->
                        <!--[if (mso)|(IE)]></td></tr></table></td></tr></table><![endif]-->
                    </div>
                </div>
            </div>
            <div style=background-color:transparent>
                <div class=block-grid style="min-width:320px;max-width:480px;overflow-wrap:break-word;word-wrap:break-word;word-break:break-word;Margin:0 auto;background-color:#fff">
                    <div style=border-collapse:collapse;display:table;width:100%;background-color:#fff>
                        <!--[if (mso)|(IE)]><table width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:transparent;"><tr><td align="center"><table cellpadding="0" cellspacing="0" border="0" style="width:480px"><tr class="layout-full-width" style="background-color:#ffffff"><![endif]-->
                        <!--[if (mso)|(IE)]><td align="center" width="480" style="background-color:#ffffff;width:480px; border-top: 0px solid transparent; border-left: 0px solid transparent; border-bottom: 0px solid transparent; border-right: 0px solid transparent;" valign="top"><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 0px; padding-left: 0px; padding-top:5px; padding-bottom:5px;"><![endif]-->
                        <div class="col num12" style=min-width:320px;max-width:480px;display:table-cell;vertical-align:top;width:480px>
                            <div class=col_cont style=width:100%!important>
                                <!--[if (!mso)&(!IE)]><!-->
                                <div style="border-top:0 solid transparent;border-left:0 solid transparent;border-bottom:0 solid transparent;border-right:0 solid transparent;padding-top:5px;padding-bottom:5px;padding-right:0;padding-left:0">
                                    <!--<![endif]-->
                                    <a href=${checklist} style=text-decoration:none;cursor:pointer>
                                        <div align=left class=button-container style=padding-top:0;padding-right:32px;padding-bottom:24px;padding-left:32px>
                                            <!--[if mso]><table width="100%" cellpadding="0" cellspacing="0" border="0" style="border-spacing: 0; border-collapse: collapse; mso-table-lspace:0pt; mso-table-rspace:0pt;"><tr><td style="padding-top: 0px; padding-right: 32px; padding-bottom: 24px; padding-left: 32px" align="left"><v:roundrect xmlns:v="urn:schemas-microsoft-com:vml" xmlns:w="urn:schemas-microsoft-com:office:word" href="" style="height:39pt; width:129.75pt; v-text-anchor:middle;" arcsize="8%" stroke="false" fillcolor="#1d84ff"><w:anchorlock/><v:textbox inset="0,0,0,0"><center style="color:#ffffff; font-family:Arial, sans-serif; font-size:14px"><![endif]-->
                                            <div style="text-decoration:none;display:inline-block;color:#fff;background-color:#1d84ff;border-radius:4px;-webkit-border-radius:4px;-moz-border-radius:4px;width:auto;width:auto;border-top:0 dotted #fff;border-right:0 dotted #fff;border-bottom:0 dotted #fff;border-left:0 dotted #fff;padding-top:10px;padding-bottom:10px;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;text-align:center;mso-border-alt:none;word-break:keep-all"><span style=padding-left:20px;padding-right:20px;font-size:14px;display:inline-block><span style="font-size:16px;line-height:2;word-break:break-word;font-family:Nunito,Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:32px"><span data-mce-style="font-size: 14px; line-height: 28px;" style=font-size:14px;line-height:28px>Initiate Signing</span></span></span></div>
                                            <!--[if mso]></center></v:textbox></v:roundrect></td></tr></table><![endif]-->
                                        </div>
                                    </a>
                                    <!--[if (!mso)&(!IE)]><!-->
                                </div>
                                <!--<![endif]-->
                            </div>
                        </div>
                        <!--[if (mso)|(IE)]></td></tr></table><![endif]-->
                        <!--[if (mso)|(IE)]></td></tr></table></td></tr></table><![endif]-->
                    </div>
                </div>
            </div>
            <div style=background-color:transparent>
                <div class=block-grid style="min-width:320px;max-width:480px;overflow-wrap:break-word;word-wrap:break-word;word-break:break-word;Margin:0 auto;background-color:#fff">
                    <div style=border-collapse:collapse;display:table;width:100%;background-color:#fff>
                        <!--[if (mso)|(IE)]><table width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:transparent;"><tr><td align="center"><table cellpadding="0" cellspacing="0" border="0" style="width:480px"><tr class="layout-full-width" style="background-color:#ffffff"><![endif]-->
                        <!--[if (mso)|(IE)]><td align="center" width="480" style="background-color:#ffffff;width:480px; border-top: 0px solid transparent; border-left: 0px solid transparent; border-bottom: 0px solid transparent; border-right: 0px solid transparent;" valign="top"><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 0px; padding-left: 0px; padding-top:5px; padding-bottom:5px;"><![endif]-->
                        <div class="col num12" style=min-width:320px;max-width:480px;display:table-cell;vertical-align:top;width:480px>
                            <div class=col_cont style=width:100%!important>
                                <!--[if (!mso)&(!IE)]><!-->
                                <div style="border-top:0 solid transparent;border-left:0 solid transparent;border-bottom:0 solid transparent;border-right:0 solid transparent;padding-top:5px;padding-bottom:5px;padding-right:0;padding-left:0">
                                    <!--<![endif]-->
                                    <!--[if mso]><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 32px; padding-left: 32px; padding-top: 00px; padding-bottom: 32px; font-family: Arial, sans-serif"><![endif]-->
                                    <div style="color:#000;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;line-height:1.5;padding-top:00px;padding-right:32px;padding-bottom:32px;padding-left:32px">
                                        <div style="line-height:1.5;font-size:12px;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;color:#000;mso-line-height-alt:18px">
                                            <p style="font-size:12px;line-height:1.5;word-break:break-word;text-align:left;font-family:Nunito,Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:18px;margin:0"><span style=font-size:12px>Click the above button, or copy and paste the following link on your web browser: <span style=color:#1d84ff>${checklist}</span></span></p>
                                        </div>
                                    </div>
                                    <!--[if mso]></td></tr></table><![endif]-->
                                    <!--[if (!mso)&(!IE)]><!-->
                                </div>
                                <!--<![endif]-->
                            </div>
                        </div>
                        <!--[if (mso)|(IE)]></td></tr></table><![endif]-->
                        <!--[if (mso)|(IE)]></td></tr></table></td></tr></table><![endif]-->
                    </div>
                </div>
            </div>
            <div style=background-color:transparent>
                <div class=block-grid style="min-width:320px;max-width:480px;overflow-wrap:break-word;word-wrap:break-word;word-break:break-word;Margin:0 auto;background-color:#fff">
                    <div style=border-collapse:collapse;display:table;width:100%;background-color:#fff>
                        <!--[if (mso)|(IE)]><table width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:transparent;"><tr><td align="center"><table cellpadding="0" cellspacing="0" border="0" style="width:480px"><tr class="layout-full-width" style="background-color:#ffffff"><![endif]-->
                        <!--[if (mso)|(IE)]><td align="center" width="480" style="background-color:#ffffff;width:480px; border-top: 0px solid transparent; border-left: 0px solid transparent; border-bottom: 0px solid transparent; border-right: 0px solid transparent;" valign="top"><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 0px; padding-left: 0px; padding-top:5px; padding-bottom:5px;"><![endif]-->
                        <div class="col num12" style=min-width:320px;max-width:480px;display:table-cell;vertical-align:top;width:480px>
                            <div class=col_cont style=width:100%!important>
                                <!--[if (!mso)&(!IE)]><!-->
                                <div style="border-top:0 solid transparent;border-left:0 solid transparent;border-bottom:0 solid transparent;border-right:0 solid transparent;padding-top:5px;padding-bottom:5px;padding-right:0;padding-left:0">
                                    <!--<![endif]-->
                                    <!--[if mso]><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 32px; padding-left: 32px; padding-top: 20px; padding-bottom: 60px; font-family: Arial, sans-serif"><![endif]-->
                                    <div style="color:#000;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;line-height:1.2;padding-top:20px;padding-right:32px;padding-bottom:60px;padding-left:32px">
                                        <div style="line-height:1.2;font-size:12px;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;color:#000;mso-line-height-alt:14px">
                                            <p style="font-size:14px;line-height:1.2;word-break:break-word;font-family:Nunito,Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:17px;margin:0"><span style=font-size:14px>Warm Regards,</span></p>
                                            <p style="font-size:14px;line-height:1.2;word-break:break-word;font-family:Nunito,Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:17px;margin:0"><span style=font-size:14px>CLEEN</span></p>
                                        </div>
                                    </div>
                                    <!--[if mso]></td></tr></table><![endif]-->
                                    <!--[if (!mso)&(!IE)]><!-->
                                </div>
                                <!--<![endif]-->
                            </div>
                        </div>
                        <!--[if (mso)|(IE)]></td></tr></table><![endif]-->
                        <!--[if (mso)|(IE)]></td></tr></table></td></tr></table><![endif]-->
                    </div>
                </div>
            </div>
            <div style=background-color:#f4f4f4>
                <div class=block-grid style="min-width:320px;max-width:480px;overflow-wrap:break-word;word-wrap:break-word;word-break:break-word;Margin:0 auto;background-color:#f4f4f4">
                    <div style=border-collapse:collapse;display:table;width:100%;background-color:#f4f4f4>
                        <!--[if (mso)|(IE)]><table width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f4f4f4;"><tr><td align="center"><table cellpadding="0" cellspacing="0" border="0" style="width:480px"><tr class="layout-full-width" style="background-color:#f4f4f4"><![endif]-->
                        <!--[if (mso)|(IE)]><td align="center" width="480" style="background-color:#f4f4f4;width:480px; border-top: 0px solid transparent; border-left: 0px solid transparent; border-bottom: 0px solid transparent; border-right: 0px solid transparent;" valign="top"><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 0px; padding-left: 0px; padding-top:5px; padding-bottom:5px;"><![endif]-->
                        <div class="col num12" style=min-width:320px;max-width:480px;display:table-cell;vertical-align:top;width:480px>
                            <div class=col_cont style=width:100%!important>
                                <!--[if (!mso)&(!IE)]><!-->
                                <div style="border-top:0 solid transparent;border-left:0 solid transparent;border-bottom:0 solid transparent;border-right:0 solid transparent;padding-top:5px;padding-bottom:5px;padding-right:0;padding-left:0">
                                    <!--<![endif]-->
                                    <!--[if mso]><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 32px; padding-left: 32px; padding-top: 20px; padding-bottom: 20px; font-family: Arial, sans-serif"><![endif]-->
                                    <div style="color:#000;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;line-height:1.2;padding-top:20px;padding-right:32px;padding-bottom:20px;padding-left:32px">
                                        <div style="line-height:1.2;font-size:12px;color:#000;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:14px">
                                            <p style=line-height:1.2;word-break:break-word;text-align:center;mso-line-height-alt:14px;margin:0><span style=color:#000>To log in to CLEEN, open this link in a web browser (e.g. Google Chrome) - <span style=color:#1d84ff>${streemlogin}</span></span></p>
                                        </div>
                                    </div>
                                    <!--[if mso]></td></tr></table><![endif]-->
                                    <!--[if (!mso)&(!IE)]><!-->
                                </div>
                                <!--<![endif]-->
                            </div>
                        </div>
                        <!--[if (mso)|(IE)]></td></tr></table><![endif]-->
                        <!--[if (mso)|(IE)]></td></tr></table></td></tr></table><![endif]-->
                    </div>
                </div>
            </div>
            <!--[if (mso)|(IE)]></td></tr></table><![endif]-->
        </td>
    </tr>
    </tbody>
</table>
<!--[if (IE)]></div><![endif]-->
</body>
</html>');

--changeset sathyam:02-seed-data-13
INSERT INTO email_templates(id, name, content)
values (10, 'REVIEWER_UNASSIGNED_FROM_CHECKLIST', '
<!doctype html>
<html xmlns=http://www.w3.org/1999/xhtml xmlns:o=urn:schemas-microsoft-com:office:office xmlns:v=urn:schemas-microsoft-com:vml>
<head>
    <!--[if gte mso 9]><xml><o:OfficeDocumentSettings><o:AllowPNG/><o:PixelsPerInch>96</o:PixelsPerInch></o:OfficeDocumentSettings></xml><![endif]-->
    <meta content="text/html; charset=utf-8" http-equiv=Content-Type>
    <meta content="width=device-width" name=viewport>
    <!--[if !mso]><!-->
    <meta content="IE=edge" http-equiv=X-UA-Compatible>
    <!--<![endif]-->
    <title></title>
    <!--[if !mso]><!-->
    <link href="https://fonts.googleapis.com/css?family=Nunito" rel=stylesheet>
    <!--<![endif]-->
    <style>body{margin:0;padding:0}table,td,tr{vertical-align:top;border-collapse:collapse}*{line-height:inherit}a[x-apple-data-detectors=true]{color:inherit!important;text-decoration:none!important}</style>
    <style id=media-query>@media (max-width:500px){.block-grid,.col{min-width:320px!important;max-width:100%!important;display:block!important}.block-grid{width:100%!important}.col{width:100%!important}.col_cont{margin:0 auto}img.fullwidth,img.fullwidthOnMobile{max-width:100%!important}.no-stack .col{min-width:0!important;display:table-cell!important}.no-stack.two-up .col{width:50%!important}.no-stack .col.num2{width:16.6%!important}.no-stack .col.num3{width:25%!important}.no-stack .col.num4{width:33%!important}.no-stack .col.num5{width:41.6%!important}.no-stack .col.num6{width:50%!important}.no-stack .col.num7{width:58.3%!important}.no-stack .col.num8{width:66.6%!important}.no-stack .col.num9{width:75%!important}.no-stack .col.num10{width:83.3%!important}.video-block{max-width:none!important}.mobile_hide{min-height:0;max-height:0;max-width:0;display:none;overflow:hidden;font-size:0}.desktop_hide{display:block!important;max-height:none!important}}</style>
</head>
<body class=clean-body style=margin:0;padding:0;-webkit-text-size-adjust:100%;background-color:#f4f4f4>
<!--[if IE]><div class="ie-browser"><![endif]-->
<table bgcolor=#f4f4f4 cellpadding=0 cellspacing=0 class=nl-container role=presentation style=table-layout:fixed;vertical-align:top;min-width:320px;border-spacing:0;border-collapse:collapse;mso-table-lspace:0;mso-table-rspace:0;background-color:#f4f4f4;width:100% valign=top width=100%>
    <tbody>
    <tr style=vertical-align:top valign=top>
        <td style=word-break:break-word;vertical-align:top valign=top>
            <!--[if (mso)|(IE)]><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td align="center" style="background-color:#f4f4f4"><![endif]-->
            <div style=background-color:#f4f4f4>
                <div class=block-grid style="min-width:320px;max-width:480px;overflow-wrap:break-word;word-wrap:break-word;word-break:break-word;Margin:0 auto;background-color:#f4f4f4">
                    <div style=border-collapse:collapse;display:table;width:100%;background-color:#f4f4f4>
                        <!--[if (mso)|(IE)]><table width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f4f4f4;"><tr><td align="center"><table cellpadding="0" cellspacing="0" border="0" style="width:480px"><tr class="layout-full-width" style="background-color:#f4f4f4"><![endif]-->
                        <!--[if (mso)|(IE)]><td align="center" width="480" style="background-color:#f4f4f4;width:480px; border-top: 0px solid transparent; border-left: 0px solid transparent; border-bottom: 0px solid transparent; border-right: 0px solid transparent;" valign="top"><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 0px; padding-left: 0px; padding-top:50px; padding-bottom:0px;"><![endif]-->
                        <div class="col num12" style=min-width:320px;max-width:480px;display:table-cell;vertical-align:top;width:480px>
                            <div class=col_cont style=width:100%!important>
                                <!--[if (!mso)&(!IE)]><!-->
                                <div style="border-top:0 solid transparent;border-left:0 solid transparent;border-bottom:0 solid transparent;border-right:0 solid transparent;padding-top:50px;padding-bottom:0;padding-right:0;padding-left:0">
                                    <!--<![endif]-->
                                    <div></div>
                                    <!--[if (!mso)&(!IE)]><!-->
                                </div>
                                <!--<![endif]-->
                            </div>
                        </div>
                        <!--[if (mso)|(IE)]></td></tr></table><![endif]-->
                        <!--[if (mso)|(IE)]></td></tr></table></td></tr></table><![endif]-->
                    </div>
                </div>
            </div>
            <div style=background-color:#f4f4f4>
                <div class=block-grid style="min-width:320px;max-width:480px;overflow-wrap:break-word;word-wrap:break-word;word-break:break-word;Margin:0 auto;background-color:#fff">
                    <div style=border-collapse:collapse;display:table;width:100%;background-color:#fff>
                        <!--[if (mso)|(IE)]><table width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f4f4f4;"><tr><td align="center"><table cellpadding="0" cellspacing="0" border="0" style="width:480px"><tr class="layout-full-width" style="background-color:#ffffff"><![endif]-->
                        <!--[if (mso)|(IE)]><td align="center" width="480" style="background-color:#ffffff;width:480px; border-top: 0px solid transparent; border-left: 0px solid transparent; border-bottom: 0px solid transparent; border-right: 0px solid transparent;" valign="top"><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 0px; padding-left: 0px; padding-top:32px; padding-bottom:32px;"><![endif]-->
                        <div class="col num12" style=min-width:320px;max-width:480px;display:table-cell;vertical-align:top;width:480px>
                            <div class=col_cont style=width:100%!important>
                                <!--[if (!mso)&(!IE)]><!-->
                                <div style="border-top:0 solid transparent;border-left:0 solid transparent;border-bottom:0 solid transparent;border-right:0 solid transparent;padding-top:32px;padding-bottom:32px;padding-right:0;padding-left:0">
                                    <!--<![endif]-->
                                    <div align=center class="img-container center fixedwidth" style=padding-right:0;padding-left:0>
                                        <!--[if mso]><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr style="line-height:0px"><td style="padding-right: 0px;padding-left: 0px;" align="center"><![endif]--><img align=center alt="Alternate text" border=0 class="center fixedwidth" src=cid:streem-color style=text-decoration:none;-ms-interpolation-mode:bicubic;height:auto;border:0;width:100%;max-width:144px;display:block title="Alternate text" width=144>
                                        <!--[if mso]></td></tr></table><![endif]-->
                                    </div>
                                    <!--[if (!mso)&(!IE)]><!-->
                                </div>
                                <!--<![endif]-->
                            </div>
                        </div>
                        <!--[if (mso)|(IE)]></td></tr></table><![endif]-->
                        <!--[if (mso)|(IE)]></td></tr></table></td></tr></table><![endif]-->
                    </div>
                </div>
            </div>
            <div style=background-color:#f4f4f4>
                <div class=block-grid style="min-width:320px;max-width:480px;overflow-wrap:break-word;word-wrap:break-word;word-break:break-word;Margin:0 auto;background-color:#fff">
                    <div style=border-collapse:collapse;display:table;width:100%;background-color:#fff>
                        <!--[if (mso)|(IE)]><table width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f4f4f4;"><tr><td align="center"><table cellpadding="0" cellspacing="0" border="0" style="width:480px"><tr class="layout-full-width" style="background-color:#ffffff"><![endif]-->
                        <!--[if (mso)|(IE)]><td align="center" width="480" style="background-color:#ffffff;width:480px; border-top: 0px solid transparent; border-left: 0px solid transparent; border-bottom: 0px solid transparent; border-right: 0px solid transparent;" valign="top"><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 0px; padding-left: 0px; padding-top:5px; padding-bottom:5px;"><![endif]-->
                        <div class="col num12" style=min-width:320px;max-width:480px;display:table-cell;vertical-align:top;width:480px>
                            <div class=col_cont style=width:100%!important>
                                <!--[if (!mso)&(!IE)]><!-->
                                <div style="border-top:0 solid transparent;border-left:0 solid transparent;border-bottom:0 solid transparent;border-right:0 solid transparent;padding-top:5px;padding-bottom:5px;padding-right:0;padding-left:0">
                                    <!--<![endif]-->
                                    <!--[if mso]><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 40px; padding-left: 40px; padding-top: 0px; padding-bottom: 32px; font-family: Arial, sans-serif"><![endif]-->
                                    <div style="color:#000;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;line-height:1.2;padding-top:0;padding-right:40px;padding-bottom:32px;padding-left:40px">
                                        <div style="line-height:1.2;font-size:12px;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;color:#000;mso-line-height-alt:14px">
                                            <p style="font-size:20px;line-height:1.2;word-break:break-word;text-align:center;font-family:Nunito,Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:24px;margin:0"><span style=font-size:20px>You were unassigned from a Prototype you were assigned to earlier</span></p>
                                        </div>
                                    </div>
                                    <!--[if mso]></td></tr></table><![endif]-->
                                    <table border=0 cellpadding=0 cellspacing=0 class=divider role=presentation style=table-layout:fixed;vertical-align:top;border-spacing:0;border-collapse:collapse;mso-table-lspace:0;mso-table-rspace:0;min-width:100%;-ms-text-size-adjust:100%;-webkit-text-size-adjust:100% valign=top width=100%>
                                        <tbody>
                                        <tr style=vertical-align:top valign=top>
                                            <td class=divider_inner style=word-break:break-word;vertical-align:top;min-width:100%;-ms-text-size-adjust:100%;-webkit-text-size-adjust:100%;padding-top:0;padding-right:10px;padding-bottom:0;padding-left:10px valign=top>
                                                <table align=center border=0 cellpadding=0 cellspacing=0 class=divider_content role=presentation style="table-layout:fixed;vertical-align:top;border-spacing:0;border-collapse:collapse;mso-table-lspace:0;mso-table-rspace:0;border-top:1px solid #eee;width:100%" valign=top width=100%>
                                                    <tbody>
                                                    <tr style=vertical-align:top valign=top>
                                                        <td style=word-break:break-word;vertical-align:top;-ms-text-size-adjust:100%;-webkit-text-size-adjust:100% valign=top><span></span></td>
                                                    </tr>
                                                    </tbody>
                                                </table>
                                            </td>
                                        </tr>
                                        </tbody>
                                    </table>
                                    <!--[if (!mso)&(!IE)]><!-->
                                </div>
                                <!--<![endif]-->
                            </div>
                        </div>
                        <!--[if (mso)|(IE)]></td></tr></table><![endif]-->
                        <!--[if (mso)|(IE)]></td></tr></table></td></tr></table><![endif]-->
                    </div>
                </div>
            </div>
            <div style=background-color:transparent>
                <div class=block-grid style="min-width:320px;max-width:480px;overflow-wrap:break-word;word-wrap:break-word;word-break:break-word;Margin:0 auto;background-color:#fff">
                    <div style=border-collapse:collapse;display:table;width:100%;background-color:#fff>
                        <!--[if (mso)|(IE)]><table width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:transparent;"><tr><td align="center"><table cellpadding="0" cellspacing="0" border="0" style="width:480px"><tr class="layout-full-width" style="background-color:#ffffff"><![endif]-->
                        <!--[if (mso)|(IE)]><td align="center" width="480" style="background-color:#ffffff;width:480px; border-top: 0px solid transparent; border-left: 0px solid transparent; border-bottom: 0px solid transparent; border-right: 0px solid transparent;" valign="top"><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 0px; padding-left: 0px; padding-top:5px; padding-bottom:5px;"><![endif]-->
                        <div class="col num12" style=min-width:320px;max-width:480px;display:table-cell;vertical-align:top;width:480px>
                            <div class=col_cont style=width:100%!important>
                                <!--[if (!mso)&(!IE)]><!-->
                                <div style="border-top:0 solid transparent;border-left:0 solid transparent;border-bottom:0 solid transparent;border-right:0 solid transparent;padding-top:5px;padding-bottom:5px;padding-right:0;padding-left:0">
                                    <!--<![endif]-->
                                    <!--[if mso]><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 32px; padding-left: 32px; padding-top: 32px; padding-bottom: 24px; font-family: Arial, sans-serif"><![endif]-->
                                    <div style="color:#000;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;line-height:1.5;padding-top:32px;padding-right:32px;padding-bottom:24px;padding-left:32px">
                                        <div style="line-height:1.5;font-size:12px;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;color:#000;mso-line-height-alt:18px">
                                            <p style="font-size:14px;line-height:1.5;word-break:break-word;font-family:Nunito,Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:21px;margin:0">Hello,</p>
                                            <p style="font-size:14px;line-height:1.5;word-break:break-word;font-family:Nunito,Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:21px;margin:0"> </p>
                                            <p style="font-size:14px;line-height:1.5;word-break:break-word;font-family:Nunito,Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:21px;margin:0">You were unassigned from a Prototype that you were assigned to earlier. Please contact your Facility Admin to know more about this.</p>
                                            <p style="font-size:14px;line-height:1.5;word-break:break-word;font-family:Nunito,Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:21px;margin:0"> </p>
                                            <p style="font-size:14px;line-height:1.5;word-break:break-word;font-family:Nunito,Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:21px;margin:0">Warm Regards,</p>
                                            <p style="font-size:14px;line-height:1.5;word-break:break-word;font-family:Nunito,Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:21px;margin:0">CLEEN</p>
                                        </div>
                                    </div>
                                    <!--[if mso]></td></tr></table><![endif]-->
                                    <!--[if (!mso)&(!IE)]><!-->
                                </div>
                                <!--<![endif]-->
                            </div>
                        </div>
                        <!--[if (mso)|(IE)]></td></tr></table><![endif]-->
                        <!--[if (mso)|(IE)]></td></tr></table></td></tr></table><![endif]-->
                    </div>
                </div>
            </div>
            <div style=background-color:#f4f4f4>
                <div class=block-grid style="min-width:320px;max-width:480px;overflow-wrap:break-word;word-wrap:break-word;word-break:break-word;Margin:0 auto;background-color:#f4f4f4">
                    <div style=border-collapse:collapse;display:table;width:100%;background-color:#f4f4f4>
                        <!--[if (mso)|(IE)]><table width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f4f4f4;"><tr><td align="center"><table cellpadding="0" cellspacing="0" border="0" style="width:480px"><tr class="layout-full-width" style="background-color:#f4f4f4"><![endif]-->
                        <!--[if (mso)|(IE)]><td align="center" width="480" style="background-color:#f4f4f4;width:480px; border-top: 0px solid transparent; border-left: 0px solid transparent; border-bottom: 0px solid transparent; border-right: 0px solid transparent;" valign="top"><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 0px; padding-left: 0px; padding-top:5px; padding-bottom:5px;"><![endif]-->
                        <div class="col num12" style=min-width:320px;max-width:480px;display:table-cell;vertical-align:top;width:480px>
                            <div class=col_cont style=width:100%!important>
                                <!--[if (!mso)&(!IE)]><!-->
                                <div style="border-top:0 solid transparent;border-left:0 solid transparent;border-bottom:0 solid transparent;border-right:0 solid transparent;padding-top:5px;padding-bottom:5px;padding-right:0;padding-left:0">
                                    <!--<![endif]-->
                                    <!--[if mso]><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="padding-right: 32px; padding-left: 32px; padding-top: 20px; padding-bottom: 20px; font-family: Arial, sans-serif"><![endif]-->
                                    <div style="color:#000;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;line-height:1.2;padding-top:20px;padding-right:32px;padding-bottom:20px;padding-left:32px">
                                        <div style="line-height:1.2;font-size:12px;color:#000;font-family:''Nunito'',Arial,''Helvetica Neue'',Helvetica,sans-serif;mso-line-height-alt:14px">
                                            <p style=line-height:1.2;word-break:break-word;text-align:center;mso-line-height-alt:14px;margin:0><span style=color:#000>To log in to CLEEN, open this link in a web browser (e.g. Google Chrome) - <span style=color:#1d84ff>${streemlogin}</span></span></p>
                                        </div>
                                    </div>
                                    <!--[if mso]></td></tr></table><![endif]-->
                                    <!--[if (!mso)&(!IE)]><!-->
                                </div>
                                <!--<![endif]-->
                            </div>
                        </div>
                        <!--[if (mso)|(IE)]></td></tr></table><![endif]-->
                        <!--[if (mso)|(IE)]></td></tr></table></td></tr></table><![endif]-->
                    </div>
                </div>
            </div>
            <!--[if (mso)|(IE)]></td></tr></table><![endif]-->
        </td>
    </tr>
    </tbody>
</table>
<!--[if (IE)]></div><![endif]-->
</body>
</html>');

