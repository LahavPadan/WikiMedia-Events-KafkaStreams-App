import json
from dataclasses import dataclass
from dataclasses_jsonschema import JsonSchemaMixin
from pprint import pprint

BOOTSTRAP_SERVERS = ''
EVENTS_TO_PRODUCE = 10

with open('wikiLangs.json', 'r', encoding="utf8") as f:
  language_codes = json.load(f)

from urllib.parse import urlparse, parse_qs


TOPIC_URLs = {'recentchange': "https://stream.wikimedia.org/v2/stream/mediawiki.recentchange",
              'revision' : "https://stream.wikimedia.org/v2/stream/mediawiki.page-create"}


# JsonSchemaMixin
@dataclass
class WikiEvent():
    # Url: str
    PageTitle: str
    IsBot: bool
    UserName: str
    Date: str
    # Timestamp: int
    Lang: str = "English"
    # Comment: str = ""
    Type: str = ""
    IsRevert: bool = False
    RevertDetails: bool = ""

    def __init__(self, eventId, event, topic_url):
        url = event['meta']['uri']
        if topic_url == TOPIC_URLs['recentchange']:
            self.Type = event['type']
            # self.Url = url
            self.PageTitle = event['title']
            self.IsBot = event['bot']
            self.UserName = event['user']
            # self.Comment = event.get('comment', "")
            # self.Timestamp = eventId[0]['timestamp']
            self.Date = event['meta']['dt']
            self.Lang = next((language_codes[code] for code in language_codes if code in urlparse(url).netloc), self.Lang)

        elif topic_url == TOPIC_URLs['revision']:
            # self.Url = url
            self.PageTitle = event['page_title']
            self.IsBot = event['performer']['user_is_bot']
            self.UserName = event['performer']['user_text']
            self.IsRevert = event.get('rev_is_revert', False)
            self.RevertDetails = event.get('rev_revert_details', "")
            # self.Comment = event.get('comment', "")
            # self.Timestamp = eventId[0]['timestamp']
            self.Date = event['meta']['dt']
            self.Lang = next((language_codes[code] for code in language_codes if code in urlparse(url).netloc), self.Lang)

    def toJSON(self):
        return json.dumps(self.__dict__, sort_keys=True, indent=4).encode('utf-8')
    

if __name__ == "__main__":
    pprint(WikiEvent.json_schema())






