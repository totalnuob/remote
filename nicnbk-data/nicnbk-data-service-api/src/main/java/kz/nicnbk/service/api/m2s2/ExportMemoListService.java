package kz.nicnbk.service.api.m2s2;

import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.m2s2.MeetingMemoDto;

import java.io.ByteArrayInputStream;
import java.util.List;

public interface ExportMemoListService extends BaseService {

    ByteArrayInputStream excelExport(List<MeetingMemoDto> list);
}
