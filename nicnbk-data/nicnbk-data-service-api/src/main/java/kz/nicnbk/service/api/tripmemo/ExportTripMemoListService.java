package kz.nicnbk.service.api.tripmemo;

import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.tripmemo.TripMemoDto;

import java.io.ByteArrayInputStream;
import java.util.List;

public interface ExportTripMemoListService extends BaseService {

    ByteArrayInputStream excelExport(List<TripMemoDto> list);
}
